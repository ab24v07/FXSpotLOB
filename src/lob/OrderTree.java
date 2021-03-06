package lob;

import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.lang.IllegalArgumentException;

public class OrderTree {
	TreeMap<Double, OrderList> priceTree = new TreeMap<Double, OrderList>();
	HashMap<Double, OrderList> priceMap = new HashMap<Double, OrderList>();;
	HashMap<Long, Order> orderMap = new HashMap<Long, Order>();
	int volume;
	int nOrders;
	int depth;
	
	public OrderTree() {
		reset();
	}
	
	public void reset() {
		priceTree.clear();
		priceMap.clear();
		orderMap.clear();
		volume = 0;
		nOrders = 0;
		depth = 0;
	}
	
	public Integer length() {
		return orderMap.size();
	}
	
	public OrderList getPriceList(double price) {
		/*
		 * Returns the OrderList object associated with 'price'
		 */
		return priceMap.get(price);
	}
	
//	public Order getOrder(int id) {
//		/*
//		 * Returns the order given the order id
//		 */
//		return orderMap.get(id);
//	}
	
	private void createPrice(double price) {
		depth += 1;
		OrderList newList = new OrderList();
		priceTree.put(price, newList);
		priceMap.put(price, newList);
	}
	
	private void removePrice(double price) {
		depth -= 1;
		priceTree.remove(price);
		priceMap.remove(price);
	}
	
	public boolean priceExists(double price) {
		return priceMap.containsKey(price);
	}
	
	private boolean orderExists(long id) {
		return orderMap.containsKey(id);
	}
	
	public void insertOrder(int time, int qty, String firmId, String side,
							long orderId, double price) throws IllegalArgumentException {
		if (orderExists(orderId)) {
			// TODO is this the best way to handle this??
			removeOrderByID(orderId);
		}
		if (qty<=0) {
			throw new IllegalArgumentException("Must enter possitive qty");
		}
		nOrders += 1;
		if (!priceExists(price)) {
			createPrice(price);
		}
		Order order = new Order(orderId, time, qty, firmId, side, price);
		order.setoL(priceMap.get(price));
		priceMap.get(price).appendOrder(order);
		orderMap.put(orderId, order);
		volume += order.getQuantity();
	}
	
	public void updateOrderQty(int qty, long orderId) {
		Order order = this.orderMap.get(orderId);
		int originalVol = order.getQuantity();
		order.updateQty(qty, order.getTimestamp());
		this.volume += (order.getQuantity() - originalVol);
	}
	
//	public void updateOrder(Order orderUpdate) {
//		long idNum = orderUpdate.getOrderId();
//		double price = orderUpdate.getPrice();
//		Order order = this.orderMap.get(idNum);
//		int originalVol = order.getQuantity();
//		if (price != order.getPrice()) {
//			// Price has been updated
//			OrderList tempOL = this.priceMap.get(order.getPrice());
//			tempOL.removeOrder(order);
//			if (tempOL.getLength()==0) {
//				removePrice(order.getPrice());
//			}
//			insertOrder(orderUpdate);
//			//.insertOrder(time, qtyRemaining, firmId, side,
//		  			  this.nextQuoteID, price);
//		} else {
//			// The quantity has changed
//			order.updateQty(orderUpdate.getQuantity(), 
//					orderUpdate.getTimestamp());
//		}
//		this.volume += (order.getQuantity() - originalVol);
//	}
	
	public void removeOrderByID(long id) {
		this.nOrders -=1;
		Order order = orderMap.get(id);
		this.volume -= order.getQuantity();
		order.getoL().removeOrder(order);
		if (order.getoL().getLength() == 0) {
			this.removePrice(order.getPrice());
		}
		this.orderMap.remove(id);
	}
	
	public Double maxPrice() {
		if (this.depth>0) {
			return this.priceTree.lastKey();
		} else {
			return null;
		}
	}
	
	public Double minPrice() {
		if (this.depth>0) {
			return this.priceTree.firstKey();
		} else {
			return null;
		}
	}
	
	public OrderList maxPriceList() {
		if (this.depth>0) {
			return this.getPriceList(maxPrice());
		} else {
			return null;
		}
	}
	
	public OrderList minPriceList() {
		if (this.depth>0) {
			return this.getPriceList(minPrice());
		} else {
			return null;
		}
	}
	
	public String toString() {
		String outString = "| The Book:\n" + 
							"| Max price = " + maxPrice() +
							"\n| Min price = " + minPrice() +
							"\n| Volume in book = " + getVolume() +
							"\n| Depth of book = " + getDepth() +
							"\n| Orders in book = " + getnOrders() +
							"\n| Length of tree = " + length() + "\n";
		for (Map.Entry<Double, OrderList> entry : this.priceTree.entrySet()) {
			outString += entry.getValue().toString();
			outString += ("|\n");
		}
		return outString;
	}

	public Integer getVolume() {
		return volume;
	}

	public Integer getnOrders() {
		return nOrders;
	}

	public Integer getDepth() {
		return depth;
	}
	
}

