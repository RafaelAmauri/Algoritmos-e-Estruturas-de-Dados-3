package Props;
import java.io.*;
/*  Entidade de Grupo.
 */
public class Grupo implements Registro{
    private int idGrupo;
    private int idUsuario;
    private String nome;
    private long momentoSorteio;
    private float valor;
    private long momentoEncontro;
    private String localEncontro;
    private String observacoes;
    private boolean sorteado;
    private boolean ativo;

    public Grupo(){
        this(-1, -1, "", -1l, -1f, -1l, "", "");
    }//end of voidContructor

    public Grupo(int iG, int iU, String n, long mS, float v, long mE, String lE, String o){
        idGrupo = iG;
        idUsuario = iU;
        nome = n;
        momentoSorteio = mS;
        valor = v;
        momentoEncontro = mE;
        localEncontro = lE;
        observacoes = o;
        sorteado = false;
        ativo = true;
    }//end of full constructor

    public void setID(int id){
        idGrupo = id;
    }//end of setID

    public int getID() {
        return idGrupo;
    }//end of getID

    public int getIDUsuario(){
        return idUsuario;
    }//end of getIDUsuario

    public String chaveSecundaria(){
        return nome;
    }//end of chaveSecundaria

    public byte[] toByteArray() throws IOException{
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(ba);
        data.writeInt(idGrupo);
        data.writeInt(idUsuario);
        data.writeUTF(nome);
        data.writeLong(momentoEncontro);
        data.writeFloat(valor);
        data.writeLong(momentoSorteio);
        data.writeUTF(localEncontro);
        data.writeUTF(observacoes);
        data.writeBoolean(sorteado);
        data.writeBoolean(ativo);
        return ba.toByteArray();
    }//end of toByteArray

    public void fromByteArray(byte[] ba) throws IOException{
        ByteArrayInputStream entrada = new ByteArrayInputStream(ba);
        DataInputStream data = new DataInputStream(entrada);
        idGrupo = data.readInt();
        idUsuario = data.readInt();
        nome = data.readUTF();
        momentoEncontro = data.readLong();
        valor = data.readFloat();
        momentoSorteio = data.readLong();
        localEncontro = data.readUTF();
        observacoes = data.readUTF();
        sorteado = data.readBoolean();
        ativo = data.readBoolean();
    }//end of fromByteArray

    public String toString(){
        return nome;
    }//end of toString

    public String getNome(){
        return nome;
    }//end of getNome

    public long getMomentoSorteado(){
        return momentoSorteio;
    }//end of getMomentoSorteado

    public float getValor(){
        return valor;
    }//end of getValor

    public long getMomentoEncontro(){
        return momentoEncontro;
    }//end of getMomentoEncontro

    public String getLocal(){
        return localEncontro;
    }//end of getLocal

    public String getObservacoes(){
        return observacoes;
    } //end of getObservacoes

    public void ativar(boolean habilitar){
        ativo = habilitar;
    }//end of ativar

    public boolean estaAtivo(){
        return ativo;
    }//end of estaAtivo

    public void sortear(boolean sortear){
        sorteado = sortear;
    }//end of sortear

    public boolean sorteado(){
        return sorteado;
    }//end of sorteado
}//end of Grupo