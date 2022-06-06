package it.itsuptoyou.collections.DTO;

import java.util.HashMap;
import java.util.Map;

import it.itsuptoyou.utils.CarbonFootPrintsUtils;
import lombok.Data;

@Data
public class CarbonFootPrintDTO {
	
	private Map<Integer, Double> alimentation;	// sezione alimentazione
	/** descrizione campi alimentation
	 * 0: Pasta,riso,cereali
	 * 1: Pane e prodotti di panetteria
	 * 2: Vegetali, patate e frutta
	 * 3: Legumi
	 * 4: Latte, yogurt
	 * 5: Burro, formaggi
	 * 6: Carne(manzo)
	 * 7: Carne(pollame,tacchino,ecc)
	 * 8: Carne(maiale)
	 * 9: Pesce
	 */
	private Map<Integer, Double> transport; // sezione trasporti
	/** descrizione campi transport
	 * 0: Automobile(soli)
	 * 1: Automobile(due)
	 * 2: Automobile(tre)
	 * 3: Automobile(quattro)
	 * 4: Taxi
	 * 5: Motocicletta/motorino
	 * 6: Autobus
	 * 7: Ferrovia,Tram,Metro
	 * 8: Traghetto
	 * 9: Aereo
	 */
	private Map<Integer, Double> house; //sezione casa
	/** descrizione campi house
	 * 0: Elettricità
	 * 1: Riscaldamento(gas)
	 * 2: Riscaldamento(liquido)
	 */
	public Map<Integer,Double> getGroupByName(String name) {
		Map<Integer,Double> group = new HashMap<>();
		switch (name) {
		case CarbonFootPrintsUtils.ALIMENTATION:
			group = this.alimentation;
			break;
		case CarbonFootPrintsUtils.TRANSPORT:
			group = this.transport;
			break;
		case CarbonFootPrintsUtils.HOUSE:
			group = this.house;
			break;
		default:
			break;
		}
		return group;
	}
}
