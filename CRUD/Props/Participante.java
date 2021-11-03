package Props;
import java.io.*;

/*
    Entidade de participantes e seus métodos:
 */
public class Participante implements Registro{
    private int idParticipante;
    private int idUsuario;
    private int idGrupo;
    private int idAmigo;

    public Participante(){
        this(-1, -1, -1 , -1);
    }//end of void constructor

    public Participante(int iP, int iU, int iG, int iA){
        idParticipante = iP;
        idUsuario = iU;
        idGrupo = iG;
        idAmigo = iA;
    }//end of full constructor

    public void setID(int id){
        idParticipante = id;
    }//end of setID

    public void setIDAmigo(int iA){
        idAmigo = iA;
    }//end of setIDAmigo

	public int getID(){
        return idParticipante;
    }//end of getID

    public int getIDUsuario(){
        return idUsuario;
    }//end of getID

    public int getIDGrupo(){
        return idGrupo;
    }//end of getID

    public int getIDAmigo(){
        return idAmigo;
    }//end of getID

	public String chaveSecundaria(){
        return idUsuario + "|" + idGrupo;
    }//end of chaveSecundaria

	public byte[] toByteArray() throws IOException{
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(ba);
        data.writeInt(idParticipante);
        data.writeInt(idUsuario);
        data.writeInt(idGrupo);
        data.writeInt(idAmigo);
        return ba.toByteArray();
    }//end of toByteArray

	public void fromByteArray(byte[] ba) throws IOException{
        ByteArrayInputStream bytes = new ByteArrayInputStream(ba);
        DataInputStream data = new DataInputStream(bytes);
        idParticipante = data.readInt();
        idUsuario = data.readInt();
        idGrupo = data.readInt();
        idAmigo = data.readInt();
    }//end of fromByteArray

	public String toString(){
        return "";
    }//end of toString
}//end of Participante