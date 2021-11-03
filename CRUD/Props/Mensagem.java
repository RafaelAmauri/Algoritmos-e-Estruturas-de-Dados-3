package Props;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/*
    Entidade de mensagens e seus métodos:
 */
public class Mensagem implements Registro {
    private int idMensagem;
    private int idUsuario;
    private int idGrupo;
    private long momento;
    private String mensagem;

    public Mensagem(){
        this(-1,-1,-1,(long)-1,"");
    }//end of void constructor

    public Mensagem(int iM, int iU, int iG, long mm,String m){
        idMensagem = iM;
        idUsuario = iU;
        idGrupo = iG;
        mensagem = m;
        momento = mm;
    }//end of full constructor

    public void setID(int id) {
        idMensagem = id;
    }//end of setId

    public int getID() {
        return idMensagem;
    }//end of getID

    public String chaveSecundaria() {
        return idGrupo + "|" + idUsuario;
    }//end of chaveSecundaria

    public int getIDUsuario() {
        return idUsuario;
    }//end of getIDUsuario

    public int getIDGrupo(){
        return idGrupo;
    }//end of getIDGrupo

    public String getMensagem(){
        return mensagem;
    }//end of getMensagem

    public long getMomento(){
        return momento;
    }//end of getMomento

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(ba);
        data.writeInt(idMensagem);
        data.writeInt(idUsuario);
        data.writeInt(idGrupo);
        data.writeUTF(mensagem);
        data.writeLong(momento);
        return ba.toByteArray();
    }//end of toByteArray

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bytes = new ByteArrayInputStream(ba);
        DataInputStream data = new DataInputStream(bytes);
        idMensagem = data.readInt();
        idUsuario = data.readInt();
        idGrupo = data.readInt();
        mensagem = data.readUTF();
        momento = data.readLong();
    }//end of fromByteArray
}//end of Mensagem