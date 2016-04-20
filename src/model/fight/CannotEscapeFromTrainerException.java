package model.fight;

import java.lang.Exception;

public class CannotEscapeFromTrainerException extends Exception{
	
	private static final long serialVersionUID = -3149403018349225378L;

	public CannotEscapeFromTrainerException(){
		super("You can't escape from battle when you are fighting a trainer!");
	}
}
