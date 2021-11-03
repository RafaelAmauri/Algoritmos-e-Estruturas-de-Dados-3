package Props;
import java.io.*;
/*  Entidade de Sugestão.
 */
public class Sugestao implements Registro{
    private int idSugestao;
    private int idUsuario;
    private String produto;
    private String loja;
    private float valor;
    private String observacoes;

    public Sugestao(){
        this(-1,-1,"","",-1,"");
    }//end of Sugestao constructor

    public Sugestao(int idS, int idU, String p, String l, float v, String o){
        idSugestao = idS;
        idUsuario = idU;
        produto = p;
        loja = l;
        valor = v;
        observacoes = o;
    }//end of Sugestao full constructor

    public void setID(int id){
        idSugestao = id;
    }//end of setId

	public int getID(){
        return idSugestao;
    }//end of getID

	public String chaveSecundaria(){
        return idUsuario + " | " + produto;
    }//end of chaveSecundaria

    public String getNome(){
        return produto;
    }//end of getNome

    public String getLoja(){
        return loja;
    }//end of getLoaja

    public float getValor(){
        return valor;
    }//end of getValor

    public String getObservacoes(){
        return observacoes;
    }//end of getObservacoes
 
	public byte[] toByteArray() throws IOException{
        ByteArrayOutputStream saida = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(saida);
        data.writeInt(idSugestao);
        data.writeInt(idUsuario);
        data.writeUTF(produto);
        data.writeUTF(loja);
        data.writeFloat(valor);
        data.writeUTF(observacoes);
        return saida.toByteArray();
    }//end of toByteArray

	public void fromByteArray(byte[] ba) throws IOException{
        ByteArrayInputStream entrada = new ByteArrayInputStream(ba);
        DataInputStream data = new DataInputStream(entrada);
        idSugestao = data.readInt();
        idUsuario = data.readInt();
        produto = data.readUTF();
        loja = data.readUTF();
        valor = data.readFloat();
        observacoes = data.readUTF();
    }//end of fromByteArray

	public String toString(){
        return produto + "\n" +
               "   " + loja + "\n" +
               "   " + "R$ " + valor + "\n" +
               "   " + observacoes + "\n";
    }//end of toString

}//end of Sugestao