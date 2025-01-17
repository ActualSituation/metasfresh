package org.adempiere.ad.dao;

/*
 * #%L
 * de.metas.adempiere.adempiere.base
 * %%
 * Copyright (C) 2015 metas GmbH
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

import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import org.adempiere.ad.dao.impl.CompareQueryFilter.Operator;
import org.adempiere.model.ModelColumn;
import org.compiere.model.IQuery;

import de.metas.process.PInstanceId;
import de.metas.util.lang.RepoIdAware;

/**
 *
 * @author tsa
 *
 * @param <T> model type
 */
public interface IQueryBuilder<T>
{
	/**
	 * Advice the SQL query builder, in case our filters are joined by OR, to explode them in several UNIONs.
	 *
	 * This is a huge optimization for databases like PostgreSQL which have way better performances on UNIONs instead of WHERE expressions joined by OR.
	 *
	 * Example: A query like
	 *
	 * <pre>
	 * SELECT ... FROM ... WHERE (Expression1 OR Expression2 OR Expression3)
	 * </pre>
	 *
	 * will be exploded to:
	 *
	 * <pre>
	 *  SELECT ... FROM ... WHERE Expression1
	 *  UNION DISTINCT
	 *  SELECT ... FROM ... WHERE Expression2
	 *  UNION DISTINCT
	 *  SELECT ... FROM ... WHERE Expression3
	 * </pre>
	 */
	String OPTION_Explode_OR_Joins_To_SQL_Unions = "Explode_OR_Joins_To_SQL_Unions";

	IQueryBuilder<T> copy();

	Class<T> getModelClass();

	/**
	 * @return the table name or <code>null</code>, if it shall be taken from the model class (see {@link #getModelClass()}).
	 * @TODO delete
	 */
	// String getTableName();

	Properties getCtx();

	String getTrxName();

	/**
	 * Add the given filter.
	 *
	 * @param filter
	 * @return
	 */
	IQueryBuilder<T> filter(IQueryFilter<T> filter);

	IQueryBuilder<T> addFiltersUnboxed(ICompositeQueryFilter<T> compositeFilter);

	IQueryBuilder<T> filterByClientId();

	ICompositeQueryFilter<T> getCompositeFilter();

	IQueryBuilder<T> setLimit(int limit);

	/**
	 * Sets a query option which will be used while building the query or while executing the query.
	 *
	 * NOTE: all options will be also passed to {@link IQuery} instance when it will be created.
	 *
	 * @param name
	 * @param value
	 * @see IQuery#setOptions(java.util.Map).
	 */
	IQueryBuilder<T> setOption(String name, Object value);

	/**
	 * Convenient way of calling {@link #setOption(String, Object)} with <code>value</code> = {@link Boolean#TRUE}.
	 */
	IQueryBuilder<T> setOption(String name);

	int getLimit();

	/** Make sure this instance now has an order-by-builder and return it. */
	IQueryBuilderOrderByClause<T> orderBy();

	//@formatter:off
	default IQueryBuilder<T> clearOrderBys() { orderBy().clear(); return this; }
	default IQueryBuilder<T> orderBy(final String columnName) { orderBy().addColumn(columnName); return this; }
	default IQueryBuilder<T> orderBy(final ModelColumn<T, ?> column) { orderBy().addColumn(column); return this; }
	default IQueryBuilder<T> orderByDescending(final String columnName) { orderBy().addColumnDescending(columnName); return this; }
	default IQueryBuilder<T> orderByDescending(final ModelColumn<T, ?> column) { orderBy().addColumnDescending(column.getColumnName()); return this; }
	//@formatter:on

	IQuery<T> create();

	IQueryBuilder<T> addNotEqualsFilter(String columnName, Object value);

	IQueryBuilder<T> addNotEqualsFilter(ModelColumn<T, ?> column, Object value);

	IQueryBuilder<T> addNotNull(ModelColumn<T, ?> column);

	IQueryBuilder<T> addNotNull(String columnName);

	IQueryBuilder<T> addCoalesceEqualsFilter(Object value, String... columnNames);

	IQueryBuilder<T> addEqualsFilter(String columnName, Object value, IQueryFilterModifier modifier);

	IQueryBuilder<T> addEqualsFilter(ModelColumn<T, ?> column, Object value, IQueryFilterModifier modifier);

	IQueryBuilder<T> addEqualsFilter(String columnName, Object value);

	IQueryBuilder<T> addEqualsFilter(ModelColumn<T, ?> column, Object value);

	/**
	 * Filters using the given string as a <b>substring</b>.
	 * If this "substring" behavior is too opinionated for your case, consider using e.g. {@link #addCompareFilter(String, Operator, Object)}.
	 *
	 * @param substring will be complemented with {@code %} at both the string's start and end, if the given string doesn't have them yet.
	 * @param ignoreCase if {@code true}, then {@code ILIKE} is used as operator instead of {@code LIKE}
	 */
	IQueryBuilder<T> addStringLikeFilter(String columnname, String substring, boolean ignoreCase);

	/**
	 * See {@link #addStringLikeFilter(String, String, boolean)}.
	 */
	IQueryBuilder<T> addStringLikeFilter(ModelColumn<T, ?> column, String substring, boolean ignoreCase);

	IQueryBuilder<T> addCompareFilter(String columnName, Operator operator, Object value);

	IQueryBuilder<T> addCompareFilter(ModelColumn<T, ?> column, Operator operator, Object value);

	IQueryBuilder<T> addCompareFilter(String columnName, Operator operator, Object value, IQueryFilterModifier modifier);

	IQueryBuilder<T> addCompareFilter(ModelColumn<T, ?> column, Operator operator, Object value, IQueryFilterModifier modifier);

	IQueryBuilder<T> addOnlyContextClient(Properties ctx);

	IQueryBuilder<T> addOnlyContextClient();

	IQueryBuilder<T> addOnlyContextClientOrSystem();

	IQueryBuilder<T> addOnlyActiveRecordsFilter();

	/**
	 * Filters those rows for whom the columnName's value is in given array.
	 * If no values were provided the record is accepted.
	 */
	@SuppressWarnings("unchecked")
	<V> IQueryBuilder<T> addInArrayOrAllFilter(String columnName, V... values);

	/**
	 * Filters those rows for whom the columnName's value is in given array.
	 * If no values were provided the record is rejected.
	 */
	@SuppressWarnings("unchecked")
	<V> IQueryBuilder<T> addInArrayFilter(String columnName, V... values);

	/**
	 * Filters those rows for whom the columnName's value is in given array.
	 * If no values were provided the record is accepted.
	 */
	@SuppressWarnings("unchecked")
	<V> IQueryBuilder<T> addInArrayOrAllFilter(ModelColumn<T, ?> column, V... values);

	/**
	 * Filters those rows for whom the columnName's value is in given array.
	 * If no values were provided the record is rejected.
	 *
	 * @param column
	 * @param values the values to check again also supports {@code null} value among them.
	 */
	@SuppressWarnings("unchecked")
	<V> IQueryBuilder<T> addInArrayFilter(ModelColumn<T, ?> column, V... values);

	/**
	 * Filters those rows for whom the columnName's value is in given collection.
	 * If no values were provided the record is accepted.
	 */
	<V> IQueryBuilder<T> addInArrayOrAllFilter(String columnName, Collection<V> values);

	/**
	 * Filters those rows for whom the columnName's value is in given collection.
	 * If no values were provided the record is rejected.
	 * Note: also works with {@link RepoIdAware} values.
	 */
	<V> IQueryBuilder<T> addInArrayFilter(String columnName, Collection<V> values);

	/**
	 * Filters those rows for whom the columnName's value is in given collection.
	 * If no values were provided the record is accepted.
	 * Note: also works with {@link RepoIdAware} values.
	 */
	<V> IQueryBuilder<T> addInArrayOrAllFilter(ModelColumn<T, ?> column, Collection<V> values);

	/**
	 * Filters those rows for whom the columnName's value is in given collection.
	 * If no values were provided the record is rejected.
	 * Note: also works with {@link RepoIdAware} values.
	 */
	<V> IQueryBuilder<T> addInArrayFilter(ModelColumn<T, ?> column, Collection<V> values);

	/**
	 * Notes:
	 * <li>This filter <b>will not</b> match {@code null} column values.</li>
	 * <li>If {@code values} is empty, then this filter will return {@code true} (as intuitively expected).</li>
	 * <li>Also works with {@link RepoIdAware} values.</li>
	 */
	<V> IQueryBuilder<T> addNotInArrayFilter(ModelColumn<T, ?> column, Collection<V> values);

	/**
	 * NOTE: in case <code>values</code> collection is empty this filter will return <code>true</code> (as intuitively expected).
	 *
	 * @param columnName
	 * @param values
	 * @return this
	 */
	<V> IQueryBuilder<T> addNotInArrayFilter(String columnName, Collection<V> values);

	IInSubQueryFilterClause<T, IQueryBuilder<T>> addInSubQueryFilter();

	<ST> IQueryBuilder<T> addInSubQueryFilter(String columnName, IQueryFilterModifier modifier, String subQueryColumnName, IQuery<ST> subQuery);

	/**
	 *
	 * @param columnName the key column from the "main" query
	 * @param subQueryColumnName the key column from the "sub" query
	 * @param subQuery the actual sub query
	 * @return this
	 */
	<ST> IQueryBuilder<T> addInSubQueryFilter(String columnName, String subQueryColumnName, IQuery<ST> subQuery);

	<ST> IQueryBuilder<T> addNotInSubQueryFilter(String columnName, String subQueryColumnName, IQuery<ST> subQuery);

	/**
	 *
	 * @param column the key column from the "main" query
	 * @param subQueryColumn the key column from the "sub" query
	 * @param subQuery the actual sub query
	 * @return this
	 */
	<ST> IQueryBuilder<T> addInSubQueryFilter(ModelColumn<T, ?> column, ModelColumn<ST, ?> subQueryColumn, IQuery<ST> subQuery);

	<ST> IQueryBuilder<T> addNotInSubQueryFilter(ModelColumn<T, ?> column, ModelColumn<ST, ?> subQueryColumn, IQuery<ST> subQuery);

	/**
	 * Create a new {@link IQueryBuilder} which collects models from given model column.
	 *
	 * Example: collect all invoice business partners (<code>Bill_Partner_ID</code>) from matched <code>C_Order</code>:
	 *
	 * <pre>
	 * final IQueryBuilder&lt;I_C_Order&gt; ordersQueryBuilder = ....;
	 *
	 * final List&lt;I_C_BPartner&gt; bpartners = ordersQueryBuilder
	 *   .addCollect(I_C_Order.COLUMN_Bill_Partner_ID) // an IQueryBuilder&lt;I_C_BPartner&gt; is returned here
	 *   .create() // create IQuery&lt;I_C_BPartner&gt;
	 *   .list()   // list bpartners
	 * </pre>
	 *
	 *
	 * @param column model column
	 * @return list of collected models
	 */
	<CollectedBaseType, ParentModelType> IQueryBuilder<CollectedBaseType> andCollect(ModelColumn<ParentModelType, CollectedBaseType> column);

	/**
	 * Same as {@link #andCollect(ModelColumn)} but you can specify what interface to use for returning values.
	 *
	 * @param column
	 * @param collectedType
	 * @return
	 */
	<CollectedBaseType, CollectedType extends CollectedBaseType, ParentModelType> IQueryBuilder<CollectedType> andCollect(
			ModelColumn<ParentModelType, CollectedBaseType> column,
			Class<CollectedType> collectedType);

	<CollectedType> IQueryBuilder<CollectedType> andCollect(String columnName, Class<CollectedType> collectedType);

	/**
	 * Returns a query to retrieve those records that reference the result of the query which was specified so far.<br>
	 * Example: first, configure a query builder to select a certain kind of <code>M_InOuts</code>. then use this method to retrieve not the specified inOuts, but its M_InOutLines:
	 *
	 * <pre>
	 * final IQueryBuilder&lt;I_M_InOut&gt; inoutsQueryBuilder = ....;
	 *
	 * final List&lt;I_M_InOutLine&gt; inoutLines = inoutsQueryBuilder
	 *   .andCollectChildren(I_M_InOutLine.COLUMN_M_InOut_ID, I_M_InOutLine.class) // an IQueryBuilder&lt;I_M_InOutLine&gt; is returned here
	 *   .create() // create IQuery&lt;I_M_InOutLine&gt;
	 *   .list()   // list inout lines
	 * </pre>
	 *
	 * @param linkColumnInChildTable the column in child model which will be used to join the child records to current record's primary key
	 * @param childType child model to be used
	 * @return query build for <code>ChildType</code>
	 */
	<ChildType, ExtChildType extends ChildType> IQueryBuilder<ExtChildType> andCollectChildren(ModelColumn<ChildType, ?> linkColumnInChildTable, Class<ExtChildType> childType);

	/**
	 * Returns a query to retrieve those records that reference the result of the query which was specified so far.<br>
	 * .
	 *
	 * This is a convenient version of {@link #andCollectChildren(ModelColumn, Class)} for the case when you don't have to retrieve an extended interface of the child type.
	 *
	 * @param linkColumnInChildTable the column in child model which will be used to join the child records to current record's primary key
	 * @return query build for <code>ChildType</code>
	 */
	default <ChildType> IQueryBuilder<ChildType> andCollectChildren(final ModelColumn<ChildType, ?> linkColumnInChildTable)
	{
		final Class<ChildType> childType = linkColumnInChildTable.getModelClass();
		return andCollectChildren(linkColumnInChildTable, childType);
	}

	/**
	 * Sets the join mode of this instance's internal composite filter.
	 *
	 * @return this
	 * @see ICompositeQueryFilter#setJoinOr()
	 */
	IQueryBuilder<T> setJoinOr();

	/**
	 * Sets the join mode of this instance's internal composite filter.
	 *
	 * @return this
	 * @see ICompositeQueryFilter#setJoinAnd()
	 */
	IQueryBuilder<T> setJoinAnd();

	/**
	 * Will only return records that are referenced by a <code>T_Selection</code> records which has the given selection ID.
	 */
	IQueryBuilder<T> setOnlySelection(PInstanceId pinstanceId);

	/**
	 * Start an aggregation of different columns, everything grouped by given <code>column</code>
	 *
	 * @param column
	 * @return aggregation builder
	 */
	<TargetModelType> IQueryAggregateBuilder<T, TargetModelType> aggregateOnColumn(ModelColumn<T, TargetModelType> column);

	<TargetModelType> IQueryAggregateBuilder<T, TargetModelType> aggregateOnColumn(String collectOnColumnName, Class<TargetModelType> targetModelType);

	IQueryBuilder<T> addBetweenFilter(final ModelColumn<T, ?> column, final Object valueFrom, final Object valueTo, final IQueryFilterModifier modifier);

	IQueryBuilder<T> addBetweenFilter(final String columnName, final Object valueFrom, final Object valueTo, final IQueryFilterModifier modifier);

	IQueryBuilder<T> addBetweenFilter(final ModelColumn<T, ?> column, final Object valueFrom, final Object valueTo);

	IQueryBuilder<T> addBetweenFilter(final String columnName, final Object valueFrom, final Object valueTo);

	IQueryBuilder<T> addEndsWithQueryFilter(String columnName, String endsWithString);

	/**
	 * Creates, appends and returns new composite filter.
	 *
	 * @return created composite filter
	 */
	ICompositeQueryFilter<T> addCompositeQueryFilter();

	IQueryBuilder<T> addValidFromToMatchesFilter(ModelColumn<T, ?> validFromColumn, ModelColumn<T, ?> validToColumn, Date dateToMatch);

	String getModelTableName();
}
