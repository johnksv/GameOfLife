package gol.s305089.sound;

/**
 * Exception class for reading an writing wav files.
 * A.Greensted.
 * (These javadoc comments were added by s305089)
 * @author A.Greensted
 */
public class WavFileException extends Exception
{
	public WavFileException()
	{
		super();
	}

	public WavFileException(String message)
	{
		super(message);
	}

	public WavFileException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public WavFileException(Throwable cause) 
	{
		super(cause);
	}
}
