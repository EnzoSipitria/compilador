package structures;

/**
 * 
 * clase para almacenar los tokens simples que son usados enlas sentencias de seleccion e iteracion para guardar el inicio de las mismas
 * 
 * */
public class TercetoSimple extends Terceto{

	public TercetoSimple(Object first){
		this.first = first;
		this.position = (Integer) first;
	}

	@Override
	public String toString(){
		return "("+operator+","+"["+((Integer) first).intValue()+"]"+")";
	};
	
	@Override
	public String getLexema() {
		// TODO Auto-generated method stub
		return "["+String.valueOf(this.position)+"]";
	}
}
