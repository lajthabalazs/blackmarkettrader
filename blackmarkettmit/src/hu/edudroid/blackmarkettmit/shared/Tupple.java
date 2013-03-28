package hu.edudroid.blackmarkettmit.shared;

import java.io.Serializable;

public class Tupple <S,T> implements Serializable {

	private static final long serialVersionUID = 1081671062530787042L;
	private S first;
	private T second;
	
	public Tupple(){}
	
	public Tupple(S first, T second) {
		this.first = first;
		this.second = second;
	}

	public S getFirst() {
		return first;
	}

	public void setFirst(S first) {
		this.first = first;
	}

	public T getSecond() {
		return second;
	}

	public void setSecond(T second) {
		this.second = second;
	}
}
