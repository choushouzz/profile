package UnitTests;

import warehouse.ControlSystem;
import warehouse.Order;
import warehouse.Pallet;
import warehouse.PickRequest;
import warehouse.Sequencer;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

public class SequencerTest {


	@Test
	public void testSequencerGetRequest() {
		Sequencer Alice = new Sequencer("Alice", "Sequencer");
		PickRequest pickrequest = new PickRequest();
		ControlSystem.getWarehouse().SequenceListadd(pickrequest);
		int n = ControlSystem.getWarehouse().getSequenceList().size();
		Alice.getRequest();
		assertEquals(Alice.getPickRequest(),pickrequest);
		assertTrue(ControlSystem.getWarehouse().getSequenceList().size() == n - 1);
		
	}
	
	@Test
	public void testSequencerSequence(){
		Order o1 = new Order("white","SE","2a","2b");
		Order o2 = new Order("white","SES","3a","3b");
		Order o3 = new Order("white","SEE","4a","4b");
		Order o4 = new Order("white","ESE","5a","5b");
		ArrayList<Order> op = new ArrayList<>();
		op.add(o1);
		op.add(o2);
		op.add(o3);
		op.add(o4);
		Sequencer Alice = new Sequencer("Alice", "Sequencer");
		PickRequest pickrequest = new PickRequest();
		pickrequest.setOrderPack(op);
		ControlSystem.getWarehouse().SequenceListadd(pickrequest);
		Alice.getRequest();
		Pallet pl = new Pallet();
		for (Order order: op){
			Alice.sequence(order.getFrontSku());
			pl.addFrontSkus(order.getFrontSku());
			Alice.sequence(order.getRearSku());
			pl.addRearSkus(order.getRearSku());
		}
		TreeMap<Integer,Pallet> expectedloadlist = new TreeMap<>();
		expectedloadlist.put(pickrequest.getId(), pl);
		TreeMap<Integer, Pallet> warehouseloadlist = ControlSystem.getWarehouse().getLoadList();
		for (Map.Entry<Integer, Pallet> entry : warehouseloadlist.entrySet()){
			Integer key = entry.getKey();
			if (expectedloadlist.containsKey(key)){
				assertEquals(warehouseloadlist.get(key).getFrontSkus(), expectedloadlist.get(key).getFrontSkus());
				assertEquals(warehouseloadlist.get(key).getRearSkus(), expectedloadlist.get(key).getRearSkus());
			}
		}
	}
	
	@Test
	public void testSequencerCheckTrue(){
		Sequencer Bob = new Sequencer("Bob", "Sequencer");
		Order o1 = new Order("white","SE","2a","2b");
		Order o2 = new Order("white","SES","3a","3b");
		Order o3 = new Order("white","SEE","4a","4b");
		Order o4 = new Order("white","ESE","5a","5b");
		ArrayList<Order> op = new ArrayList<>();
		op.add(o1);
		op.add(o2);
		op.add(o3);
		op.add(o4);
		Pallet pl = new Pallet();
		for (Order order: op){
			pl.addFrontSkus(order.getFrontSku());
			pl.addRearSkus(order.getRearSku());
		}
		assertTrue(Bob.check(pl));
	}
	
	@Test
	public void testSequencerCheckFalse(){
		Sequencer Bob = new Sequencer("Bob", "Sequencer");
		Order o1 = new Order("white","SE","2a","2b");
		Order o2 = new Order("white","SES","3a","3b");
		Order o3 = new Order("white","SEE","4a","4b");
		ArrayList<Order> op = new ArrayList<>();
		op.add(o1);
		op.add(o2);
		op.add(o3);
		Pallet pl = new Pallet();
		for (Order order: op){
			pl.addFrontSkus(order.getFrontSku());
			pl.addRearSkus(order.getRearSku());
		}
		assertFalse(Bob.check(pl));
	}
	
	@Test
	public void testSequencerGetRequestGetPickRequest() {
		Sequencer John = new Sequencer("John", "Sequencer");
		PickRequest pickrequest = new PickRequest();
		ControlSystem.getWarehouse().SequenceListadd(pickrequest);
		John.getRequest();
		assertEquals(John.getPickRequest(),pickrequest);
	}
	
	@Test
	public void testSequencerRescan(){
		
	}
	
}
