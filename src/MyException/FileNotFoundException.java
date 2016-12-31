package MyException;

public class FileNotFoundException extends MyException{
	public FileNotFoundException(){
		super("指定されたファイルが見つかりません。");
	}
}
