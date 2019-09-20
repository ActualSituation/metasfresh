package org.adempiere.inout.util;

import java.util.List;
import java.util.Optional;

import org.adempiere.warehouse.WarehouseId;

import de.metas.order.OrderId;
import de.metas.shipping.ShipperId;

public interface IShipmentSchedulesDuringUpdate
{

	public enum CompleteStatus
	{
		OK,
		INCOMPLETE_LINE,
		INCOMPLETE_ORDER
	}

	/**
	 * 
	 * @return a copy of the list of {@link DeliveryGroupCandidate}s stored in this instance.
	 */
	List<DeliveryGroupCandidate> getCandidates();

	/**
	 * 
	 * @return the number of {@link DeliveryGroupCandidate}s this instance contains.
	 */
	int size();

	/**
	 * Note: no need for a 'shipperID'-parameter (as in {@link #getInOutForShipper(int, int)}) because there are not two different shipperId for the same order.
	 * 
	 * @param orderId
	 * @param pPartnerAddress
	 * @return
	 */
	DeliveryGroupCandidate getInOutForOrderId(OrderId orderId, WarehouseId warehouseId, String bPartnerAddress);

	void addGroup(DeliveryGroupCandidate deliveryGroupCandidate);

	/**
	 * 
	 * @param shipperId
	 * @param bPartnerAddress
	 * @return the inOut with the given parameters
	 * @throws IllegalStateException if no inOut with the given bPartnerLocationId and shipperId has been added
	 * 
	 */
	DeliveryGroupCandidate getInOutForShipper(Optional<ShipperId> shipperId, WarehouseId warehouseId, String bPartnerAddress);

	void addLine(DeliveryLineCandidate deliveryLineCandidate);

	DeliveryLineCandidate getLineCandidateForShipmentScheduleId(int shipmentScheduleId);

	/**
	 * Adds a custom status info for the given iol. Usally the info explains, why an open order line won't be delivered this time.
	 * 
	 * @param inOutLine
	 * @param string
	 */
	void addStatusInfo(DeliveryLineCandidate inOutLine, String string);

	String getStatusInfos(DeliveryLineCandidate inOutLine);
}
