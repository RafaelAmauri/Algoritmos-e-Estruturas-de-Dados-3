package Props;
import java.io.IOException;
/*  Entidade de Registro.
 */
public interface Registro{
    public void setID(int id);
	public int getID();
	public String chaveSecundaria();
	public byte[] toByteArray() throws IOException;
	public void fromByteArray(byte[] ba) throws IOException;
	public String toString();
}//end of Entidade