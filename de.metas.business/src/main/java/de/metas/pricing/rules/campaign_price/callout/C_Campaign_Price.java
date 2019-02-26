package de.metas.pricing.rules.campaign_price.callout;

import org.adempiere.ad.callout.annotations.Callout;
import org.adempiere.ad.callout.annotations.Callout.RecursionAvoidanceLevel;
import org.adempiere.ad.callout.annotations.CalloutMethod;
import org.adempiere.ad.callout.spi.IProgramaticCalloutProvider;
import org.adempiere.ad.modelvalidator.annotations.Init;
import org.adempiere.ad.modelvalidator.annotations.Interceptor;
import org.adempiere.ad.modelvalidator.annotations.ModelChange;
import org.adempiere.exceptions.FillMandatoryException;
import org.adempiere.location.CountryId;
import org.compiere.model.I_C_Campaign_Price;
import org.compiere.model.ModelValidator;
import org.springframework.stereotype.Component;

import de.metas.adempiere.service.ICountryDAO;
import de.metas.bpartner.BPGroupId;
import de.metas.bpartner.BPartnerId;
import de.metas.money.CurrencyId;
import de.metas.util.Services;

/*
 * #%L
 * de.metas.business
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

@Interceptor(I_C_Campaign_Price.class)
@Callout(value = I_C_Campaign_Price.class, recursionAvoidanceLevel = RecursionAvoidanceLevel.CalloutClass)
@Component
public class C_Campaign_Price
{
	@Init
	public void init()
	{
		Services.get(IProgramaticCalloutProvider.class).registerAnnotatedCallout(this);
	}

	@CalloutMethod(columnNames = I_C_Campaign_Price.COLUMNNAME_C_BPartner_ID, skipIfCopying = true)
	public void onBPartnerChanged(final I_C_Campaign_Price record)
	{
		final BPartnerId bpartnerId = BPartnerId.ofRepoIdOrNull(record.getC_BPartner_ID());
		if (bpartnerId == null)
		{
			return;
		}

		record.setC_BP_Group_ID(-1);
	}

	@CalloutMethod(columnNames = I_C_Campaign_Price.COLUMNNAME_C_BP_Group_ID, skipIfCopying = true)
	public void onBPGroupChanged(final I_C_Campaign_Price record)
	{
		final BPGroupId bpGroupId = BPGroupId.ofRepoIdOrNull(record.getC_BP_Group_ID());
		if (bpGroupId == null)
		{
			return;
		}

		record.setC_BPartner_ID(-1);
	}

	@CalloutMethod(columnNames = I_C_Campaign_Price.COLUMNNAME_C_Country_ID, skipIfCopying = true)
	public void onCountryChanged(final I_C_Campaign_Price record)
	{
		final CountryId countryId = CountryId.ofRepoIdOrNull(record.getC_Country_ID());
		if (countryId == null)
		{
			return;
		}

		final ICountryDAO countriesRepo = Services.get(ICountryDAO.class);
		final CurrencyId currencyId = countriesRepo.getCountryCurrencyId(countryId).orElse(null);
		if (currencyId != null)
		{
			record.setC_Currency_ID(currencyId.getRepoId());
		}
	}

	@ModelChange(timings = { ModelValidator.TYPE_BEFORE_NEW, ModelValidator.TYPE_BEFORE_CHANGE })
	public void beforeSave(final I_C_Campaign_Price record)
	{
		if (record.getC_BPartner_ID() <= 0 && record.getC_BP_Group_ID() <= 0)
		{
			throw new FillMandatoryException(I_C_Campaign_Price.COLUMNNAME_C_BP_Group_ID);
		}
	}
}
