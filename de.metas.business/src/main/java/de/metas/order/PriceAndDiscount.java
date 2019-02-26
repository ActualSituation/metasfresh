package de.metas.order;

import static java.math.BigDecimal.ZERO;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.compiere.model.I_C_OrderLine;
import org.compiere.util.Env;
import org.compiere.util.Util;

import de.metas.currency.CurrencyPrecision;
import de.metas.pricing.limit.PriceLimitRuleResult;
import de.metas.util.lang.Percent;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/*
 * #%L
 * de.metas.swat.base
 * %%
 * Copyright (C) 2018 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

/** Order line price & discount calculations */
@Value
public class PriceAndDiscount
{
	public static PriceAndDiscount of(final I_C_OrderLine orderLine, final CurrencyPrecision precision)
	{
		return builder()
				.precision(precision)
				.priceEntered(orderLine.getPriceEntered())
				.priceActual(orderLine.getPriceActual())
				.discount(Percent.of(orderLine.getDiscount()))
				.priceLimit(orderLine.getPriceLimit())
				.build();
	}

	BigDecimal priceEntered;
	BigDecimal priceActual;
	Percent discount;
	CurrencyPrecision precision;
	BigDecimal priceLimit;

	boolean priceLimitEnforced;
	String priceLimitEnforceExplanation;

	@Builder(toBuilder = true)
	private PriceAndDiscount(
			BigDecimal priceEntered,
			BigDecimal priceActual,
			Percent discount,
			CurrencyPrecision precision,
			BigDecimal priceLimit,
			boolean priceLimitEnforced,
			String priceLimitEnforceExplanation)
	{
		this.precision = precision != null ? precision : CurrencyPrecision.ofInt(2);
		this.priceEntered = this.precision.round(Util.coalesce(priceEntered, ZERO));
		this.priceActual = this.precision.round(Util.coalesce(priceActual, ZERO));
		this.priceLimit = this.precision.round(Util.coalesce(priceLimit, ZERO));

		this.discount = Util.coalesce(discount, Percent.ZERO);
		this.priceLimitEnforced = priceLimitEnforced;
		this.priceLimitEnforceExplanation = priceLimitEnforceExplanation;
	}

	public PriceAndDiscount enforcePriceLimit(final PriceLimitRuleResult priceLimitResult)
	{
		if (!priceLimitResult.isApplicable())
		{
			return this;
		}

		final BigDecimal priceLimit = priceLimitResult.getPriceLimit();
		boolean priceLimitEnforced = false;
		BigDecimal priceEntered = this.priceEntered;
		BigDecimal priceActual = this.priceActual;
		Percent discount = this.discount;

		boolean updateDiscount = false;
		if (priceLimitResult.isBelowPriceLimit(priceEntered))
		{
			priceLimitEnforced = true;
			priceEntered = priceLimit;
			updateDiscount = true;
		}
		if (priceLimitResult.isBelowPriceLimit(priceActual))
		{
			priceLimitEnforced = true;
			priceActual = priceLimit;
			updateDiscount = true;
		}

		if (priceEntered.signum() != 0 && updateDiscount)
		{
			discount = calculateDiscountFromPrices(priceEntered, priceActual, precision);
		}

		return toBuilder()
				.priceEntered(priceEntered)
				.priceActual(priceActual)
				.discount(discount)
				.priceLimit(priceLimit)
				.priceLimitEnforced(priceLimitEnforced)
				.priceLimitEnforceExplanation(priceLimitEnforced ? priceLimitResult.getPriceLimitExplanation() : null)
				.build();
	}

	public PriceAndDiscount updatePriceActual()
	{
		final BigDecimal priceActual = discount.subtractFromBase(priceEntered, precision.toInt());
		return toBuilder().priceActual(priceActual).build();
	}

	public PriceAndDiscount updatePriceActualIfPriceEnteredIsNotZero()
	{
		if (priceEntered.signum() == 0)
		{
			return this;
		}

		return updatePriceActual();
	}

	public static Percent calculateDiscountFromPrices(final BigDecimal priceEntered, final BigDecimal priceActual, final CurrencyPrecision precision)
	{
		if (priceEntered.signum() == 0)
		{
			return Percent.ZERO;
		}

		final BigDecimal delta = priceEntered.subtract(priceActual);
		return Percent.of(delta, priceEntered, precision.toInt());
	}

	public static BigDecimal calculatePriceEnteredFromPriceActualAndDiscount(
			@NonNull final BigDecimal priceActual,
			@NonNull final BigDecimal discount,
			final int precision)
	{
		final BigDecimal multiplier = Env.ONEHUNDRED
				.add(discount)
				.divide(Env.ONEHUNDRED, 12, RoundingMode.HALF_UP);
		return priceActual
				.multiply(multiplier)
				.setScale(precision, RoundingMode.HALF_UP);
	}

	public void applyTo(final I_C_OrderLine orderLine)
	{
		orderLine.setPriceEntered(priceEntered);
		orderLine.setDiscount(discount.getValue());
		orderLine.setPriceActual(priceActual);
		orderLine.setPriceLimit(priceLimit);
	}
}
