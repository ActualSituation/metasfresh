package de.metas.invoicecandidate.externallyreferenced;

import java.time.LocalDate;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import de.metas.bpartner.service.BPartnerInfo;
import de.metas.document.DocTypeId;
import de.metas.invoicecandidate.InvoiceCandidateId;
import de.metas.lang.SOTrx;
import de.metas.money.CurrencyId;
import de.metas.order.InvoiceRule;
import de.metas.organization.OrgId;
import de.metas.pricing.PriceListVersionId;
import de.metas.pricing.PricingSystemId;
import de.metas.product.ProductId;
import de.metas.product.ProductPrice;
import de.metas.quantity.StockQtyAndUOMQty;
import de.metas.tax.api.TaxId;
import de.metas.uom.UomId;
import de.metas.util.collections.CollectionUtils;
import de.metas.util.lang.ExternalId;
import de.metas.util.lang.Percent;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/*
 * #%L
 * de.metas.swat.base
 * %%
 * Copyright (C) 2019 metas GmbH
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

@Data
public class ExternallyReferencedCandidate
{
	public static ExternallyReferencedCandidateBuilder createBuilder(@NonNull final NewManualInvoiceCandidate newIC)
	{
		return ExternallyReferencedCandidate
				.builder()
				.billPartnerInfo(newIC.getBillPartnerInfo())
				.dateOrdered(newIC.getDateOrdered())
				.discountOverride(newIC.getDiscountOverride())
				.externalHeaderId(newIC.getExternalHeaderId())
				.externalLineId(newIC.getExternalLineId())
				.invoiceDocTypeId(newIC.getInvoiceDocTypeId())
				.invoiceRuleOverride(newIC.getInvoiceRuleOverride())
				.invoicingUomId(newIC.getInvoicingUomId())
				.lineDescription(newIC.getLineDescription())
				.orgId(newIC.getOrgId())
				.poReference(newIC.getPoReference())
				.presetDateInvoiced(newIC.getPresetDateInvoiced())
				.priceEnteredOverride(newIC.getPriceEnteredOverride())
				.productId(newIC.getProductId())
				.qtyDelivered(newIC.getQtyDelivered())
				.qtyOrdered(newIC.getQtyOrdered())
				.soTrx(newIC.getSoTrx());
	}

	private final OrgId orgId;

	// may be null if this instance was not loaded by the repo
	private final InvoiceCandidateId id;

	private ExternalId externalHeaderId;
	private ExternalId externalLineId;

	private String poReference;

	private final BPartnerInfo billPartnerInfo;

	private final ProductId productId;

	private final InvoiceRule invoiceRule;

	private InvoiceRule invoiceRuleOverride;

	private final SOTrx soTrx;

	private LocalDate dateOrdered;

	private LocalDate presetDateInvoiced;

	private StockQtyAndUOMQty qtyOrdered;

	private StockQtyAndUOMQty qtyDelivered;

	private final UomId invoicingUomId;

	private final PricingSystemId pricingSystemId;

	/** when loaded from DB, come IC records can have an empty priceListVersionId. */
	private final PriceListVersionId priceListVersionId;

	private final ProductPrice priceEntered;

	/** If given, then productId and currencyId have to match! */
	private ProductPrice priceEnteredOverride;

	private final ProductPrice priceActual;

	private final Percent discount;

	private final TaxId taxId;

	private Percent discountOverride;

	private DocTypeId invoiceDocTypeId;

	private String lineDescription;

	@Builder
	private ExternallyReferencedCandidate(
			@NonNull final OrgId orgId,
			@Nullable final InvoiceCandidateId id,
			@Nullable final ExternalId externalHeaderId,
			@Nullable final ExternalId externalLineId,
			@Nullable final String poReference,
			@NonNull final BPartnerInfo billPartnerInfo,
			@NonNull final ProductId productId,
			@NonNull final InvoiceRule invoiceRule,
			@Nullable final InvoiceRule invoiceRuleOverride,
			@NonNull final SOTrx soTrx,
			@NonNull final LocalDate dateOrdered,
			@Nullable final LocalDate presetDateInvoiced,
			@NonNull final StockQtyAndUOMQty qtyOrdered,
			@NonNull final StockQtyAndUOMQty qtyDelivered,
			@NonNull final UomId invoicingUomId,
			@NonNull final PricingSystemId pricingSystemId,
			@Nullable final PriceListVersionId priceListVersionId,
			@NonNull final ProductPrice priceEntered,
			@Nullable final ProductPrice priceEnteredOverride,
			@NonNull final Percent discount,
			@Nullable final Percent discountOverride,
			@NonNull final ProductPrice priceActual,
			@NonNull final TaxId taxId,
			@Nullable final DocTypeId invoiceDocTypeId,
			@Nullable final String lineDescription)
	{
		this.orgId = orgId;
		this.id = id;
		this.externalHeaderId = externalHeaderId;
		this.externalLineId = externalLineId;
		this.poReference = poReference;
		this.billPartnerInfo = billPartnerInfo;
		this.productId = productId;
		this.invoiceRule = invoiceRule;
		this.invoiceRuleOverride = invoiceRuleOverride;
		this.soTrx = soTrx;
		this.dateOrdered = dateOrdered;
		this.presetDateInvoiced = presetDateInvoiced;
		this.qtyOrdered = qtyOrdered;
		this.qtyDelivered = qtyDelivered;
		this.invoicingUomId = invoicingUomId;
		this.pricingSystemId = pricingSystemId;
		this.priceListVersionId = priceListVersionId;
		this.priceEntered = priceEntered;
		this.priceEnteredOverride = priceEnteredOverride;
		this.discount = discount;
		this.discountOverride = discountOverride;
		this.priceActual = priceActual;
		this.taxId = taxId;
		this.invoiceDocTypeId = invoiceDocTypeId;
		this.lineDescription = lineDescription;

		final CurrencyId currencyId = CollectionUtils
				.extractSingleElement(
						ImmutableList.of(priceEntered, priceActual),
						ProductPrice::getCurrencyId);

		if (priceEnteredOverride != null)
		{
			if (!priceEnteredOverride.getProductId().equals(productId))
			{
				// TODO fail
			}
			if (!priceEnteredOverride.getUomId().equals(invoicingUomId))
			{
				// TODO fail
			}
			if(!priceEnteredOverride.getCurrencyId().equals(currencyId))
			{
				// TODO fail
			}
		}
	}
}
