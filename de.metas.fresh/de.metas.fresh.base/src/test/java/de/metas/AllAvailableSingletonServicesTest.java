package de.metas;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.adempiere.test.AdempiereTestHelper;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.google.common.base.Stopwatch;

import de.metas.util.ISingletonService;
import de.metas.util.Services;
import lombok.NonNull;
import lombok.Value;

/*
 * #%L
 * de.metas.fresh.base
 * %%
 * Copyright (C) 2017 metas GmbH
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

public class AllAvailableSingletonServicesTest
{
	private static final SkipRules skipRules = new SkipRules()
			.skipServiceInterface(org.adempiere.inout.replenish.service.IReplenishForFutureQty.class, "is registered programmatically")
			.skipServiceInterface(de.metas.adempiere.service.IAppDictionaryBL.class, "is registered programmatically")
			.skipServiceInterface(de.metas.letters.api.ITextTemplateBL.class, "is registered programmatically")
			.skipServiceInterface(de.metas.product.IProductActivityProvider.class, "is registered programatically")
			.skipServiceInterface(de.metas.procurement.base.IAgentSyncBL.class, "JAX-RS")
			//
			.skipServiceInterface(org.adempiere.util.testservice.ITestServiceWithFailingConstructor.class, "because it's supposed to fail")
			.skipServiceInterface(org.adempiere.util.testservice.ITestMissingService.class, "because it's supposed to fail")
			.skipServiceInterfaceIfStartsWith("org.adempiere.util.proxy.impl.JavaAssistInterceptorTests", "some test interface")
			.skipServiceInterface(de.metas.cache.interceptor.testservices.ITestServiceWithPrivateCachedMethod.class, "some test interface")
			//
			// Skip services with no default constructor (spring components):
			.skipServiceInterface(org.eevolution.mrp.api.ILiberoMRPContextFactory.class, "spring component")
			.skipServiceInterface(de.metas.material.planning.IMRPContextFactory.class, "spring component")
			.skipServiceInterface(de.metas.document.sequence.IDocumentNoBuilderFactory.class, "spring component")
			.skipServiceInterface(de.metas.payment.esr.api.IESRImportBL.class, "spring component")
			.skipServiceInterface(de.metas.notification.INotificationRepository.class, "spring component")
			.skipServiceInterface(de.metas.inoutcandidate.api.IShipmentScheduleUpdater.class, "spring component")
			.skipServiceInterface(de.metas.inoutcandidate.invalidation.IShipmentScheduleInvalidateBL.class, "spring component")
			.skipServiceInterface(de.metas.bpartner.service.IBPartnerBL.class, "spring component")
			.skipServiceInterface(de.metas.ordercandidate.api.IOLCandBL.class, "spring component");

	@BeforeEach
	public void beforeEach()
	{
		AdempiereTestHelper.get().init();
	}

	@ParameterizedTest
	@ArgumentsSource(SingletonServiceInterfacesArgumentsProvider.class)
	public void instantiateAndValidateSingletonService(final Class<? extends ISingletonService> serviceInterfaceClass)
	{
		skipRules.assumeNotSkipped(serviceInterfaceClass);

		final ISingletonService serviceImpl = Services.get(serviceInterfaceClass);
		assertThat(serviceImpl).isNotNull();
	}

	@Value(staticConstructor = "of")
	private static class SkipRule
	{
		@NonNull
		Predicate<String> predicate;
		@Nullable
		String reason;

		public void assumeNotSkipped(final String serviceInterfaceClassname)
		{
			final boolean skipped = predicate.test(serviceInterfaceClassname);
			Assumptions.assumeTrue(!skipped, "skip because " + (reason != null ? reason : "unknown reason"));
		}
	}

	private static class SkipRules
	{
		private final List<SkipRule> skipRules = new ArrayList<>();

		public void assumeNotSkipped(final Class<? extends ISingletonService> serviceInterfaceClass)
		{
			final String serviceInterfaceClassname = serviceInterfaceClass.getName();
			for (final SkipRule skipRule : skipRules)
			{
				skipRule.assumeNotSkipped(serviceInterfaceClassname);
			}
		}

		private SkipRules skipServiceInterface(
				@NonNull final String serviceInterfaceClassnameToSkip,
				@Nullable final String reason)
		{
			skipRules.add(SkipRule.of(
					classname -> serviceInterfaceClassnameToSkip.equals(classname),
					reason));
			return this;
		}

		private SkipRules skipServiceInterface(
				@NonNull final Class<? extends ISingletonService> serviceInterfaceClass,
				@Nullable final String reason)
		{
			skipServiceInterface(serviceInterfaceClass.getName(), reason);
			return this;
		}

		private SkipRules skipServiceInterfaceIfStartsWith(
				@NonNull final String classnamePrefix,
				@Nullable final String reason)
		{
			skipRules.add(SkipRule.of(
					classname -> classname.startsWith(classnamePrefix),
					reason));
			return this;
		}
	}

	public static class SingletonServiceInterfacesArgumentsProvider implements ArgumentsProvider
	{
		@Override
		public Stream<? extends Arguments> provideArguments(ExtensionContext context)
		{
			return provideClasses().map(Arguments::of);
		}

		public Stream<Class<? extends ISingletonService>> provideClasses()
		{
			Stopwatch stopwatch = Stopwatch.createStarted();
			final Reflections reflections = new Reflections(new ConfigurationBuilder()
					.addUrls(ClasspathHelper.forClassLoader())
					.setScanners(new SubTypesScanner()));
			System.out.println("Created reflections instance in " + stopwatch);

			return reflections.getSubTypesOf(ISingletonService.class)
					.stream()
					.filter(Class::isInterface)
					.sorted(Comparator.comparing(Class::getName));
		}
	}

}
