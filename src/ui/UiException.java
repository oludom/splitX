/**
 * 
 */
package ui;

/**
 * @author Soeren Wirries
 *
 */
public class UiException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String errorTxt;
	public UiException(){
		
	}
	
	static public class WrongFormatException extends UiException{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String errorTxt;
		public WrongFormatException() {
			this.errorTxt = "Die Eingabe hatte nicht das richtige Format!";
		}
		
		public WrongFormatException(String text) {
			this.errorTxt = text;
		}
		public String toString(){
			return errorTxt;
		}
	}
	
	static public class WrongEntryException extends UiException{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		String errorTxt;
		public WrongEntryException() {
			this.errorTxt = "Die Eingabe liegt nicht im Wertebereich!";
		}
		
		public WrongEntryException(String text) {
			this.errorTxt = text;
		}
		public String toString(){
			return errorTxt;
		}
	}

}

