package structures;

public class TercetoMultiplicacion extends Terceto {
	
	public TercetoMultiplicacion(Object first, Object second){
		this.operator = "*";
		this.first    = first;
		this.second   = second;
		this.position = 0;
	}

	@Override
	public String getLexema() {
		// TODO Auto-generated method stub
		return "["+String.valueOf(this.position)+"]";
	}

}
