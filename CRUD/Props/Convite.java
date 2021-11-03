package Props;
import java.io.*;
/*  Entidade de Convite.
    Tem as propriedades de realacionamento com os grupos, e com os e-mails enviados.
    Apresenta 4 estados:
    0 - pendente
    1 - aceito
    2 - recusado
    3 - cancelado
 */
public class Convite implements Registro{
    private int idConvite;
    private int idGrupo;
    private String email;
    private long momentoConvite;
    private byte estado;

    public Convite(){
        this(-1,-1,"",-1l,(byte)-1);
    }//end of void constructor

    public Convite(int iC, int iG, String em, long mC, byte es){
        idConvite = iC;
        idGrupo = iG;
        email = em;
        momentoConvite = mC;
        estado = es;
    }//end of full constructor

    public void setID(int id){
        idConvite = id;
    }//end of setID

    public void setEstado(byte e){
        estado = e;
    }//end of setEstado

    public void setMomentoConvite(long l){
        momentoConvite = l;
    }//end of setMomentoConvite

	public int getID(){
        return idConvite;
    }//end of getID

    public int getIDGrupo(){
        return idGrupo;
    }//end of getID

	public String chaveSecundaria(){
        return email;
    }//end of chaveSecundaria

    public long getMomentoConvite(){
        return momentoConvite;
    }//end of getMomentoEncontro

    public byte getEstado(){
        return estado;
    }//end of getEstado

	public byte[] toByteArray() throws IOException{
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(ba);
        data.writeInt(idConvite);
        data.writeInt(idGrupo);
        data.writeUTF(email);
        data.writeLong(momentoConvite);
        data.writeByte(estado);
        return ba.toByteArray();
    }//end of toByteArray

	public void fromByteArray(byte[] ba) throws IOException{
        ByteArrayInputStream entrada = new ByteArrayInputStream(ba);
        DataInputStream data = new DataInputStream(entrada);
        idConvite = data.readInt();
        idGrupo = data.readInt();
        email = data.readUTF();
        momentoConvite = data.readLong();
        estado = data.readByte();
    }//end of fromByteArray;

	public String toString(){
        return email;
    }//end of toString

    public String estadoToString(){
        String sEstado = "";
        switch(estado){
            case 0:
                sEstado += "pendente";
                break;
            case 1:
                sEstado += "aceito";
                break;
            case 2:
                sEstado += "recusado";
                break;
            case 3:
                sEstado += "cancelado";
                break;
        }//end of switch
        return sEstado;
    }//end of estadoToString
}//end of Convites