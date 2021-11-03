package Props;
import java.io.*;
/*  Entidade de Usuarios.
 */
public class Usuario implements Registro{
	private int id;
	private String nome;
	private String email;
	private byte[] senha;
	private byte[] salt;
	
	public Usuario(){
		this(-1,"","",new byte[32],new byte[32]);
	}//end of void constructor
	
	public Usuario(int id,String nome,String email,byte[] senha,byte[] salt){
		this.id     =  id;
		this.nome   =  nome;
		this.email  =  email;
		this.senha  =  senha;
		this.salt   =  salt;
	}//end of full constructor
	
	public void setID(int id){
		this.id = id;
	}//end of setID
	
	public int getID(){
		return this.id;
	}//end of getID
	
	public String getNome(){
		return this.nome;
	}//end of getNome
	
	public byte[] getSenha(){
		return this.senha;
	}//end of getSenha
	
	public byte[] getSalt(){
		return this.salt;
	}//end of getSalt

	public String chaveSecundaria(){
		return this.email;
	}//end of chaveSecundaria
	
	public byte[] toByteArray() throws IOException{
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		DataOutputStream saida = new DataOutputStream(data);
		saida.writeInt(this.id);
		saida.writeUTF(this.nome);
		saida.writeUTF(this.email);
		saida.write(this.senha);
		saida.write(this.salt);
		return data.toByteArray();
	}//end of toByteArray
	
	public void fromByteArray(byte[] ba) throws IOException{
		ByteArrayInputStream data = new ByteArrayInputStream(ba);
		DataInputStream saida = new DataInputStream(data);
		this.id = saida.readInt();
		this.nome = saida.readUTF();
		this.email = saida.readUTF();
		
		saida.read(senha);
		saida.read(salt);
	}//end of fromByteArray
	
	public String toString(){
		return "Nome........: " + this.nome + "\n" +
				 "E-mail......: " + this.email + "\n" +
				 "Senha.......: " + " Essa não pode mostrar né kkk";
	}//end of toString
}//end of class
