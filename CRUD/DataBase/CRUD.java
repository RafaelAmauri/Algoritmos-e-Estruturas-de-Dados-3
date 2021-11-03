package DataBase;
import java.io.*;
import java.lang.reflect.*;
import Props.*;

public class CRUD<T extends Registro>{
	private final String diretorio = "dados";
	private Indice_Lapides lixo;
	private int minimo_sobreescrita = 85;
	private RandomAccessFile arquivo;
	private HashExtensivel indiceDireto;
	private ArvoreBMais_String_Int indiceIndireto;
	private final int tamanhoCabecalho = 12;
	private Constructor<T> construtor; 
	
	public CRUD(String nomeArquivo, Constructor<T> construtor) throws Exception{
		File d           =  new File(this.diretorio);
		if(!d.exists())
			d.mkdir();
		this.lixo        =  new Indice_Lapides(minimo_sobreescrita, this.diretorio +"/" + nomeArquivo + "_Lixo.db");
		this.construtor  =  construtor;
		arquivo = new RandomAccessFile(this.diretorio+"/"+nomeArquivo+".db", "rw");
		if(arquivo.length()<tamanhoCabecalho){
      		arquivo.writeInt(0);  // cabeçalho
			arquivo.writeLong(-1);
		}//end of if
		indiceDireto = new HashExtensivel(10, this.diretorio+"/diretorio."+nomeArquivo+".idx", 
                           				 this.diretorio+"/cestos."+nomeArquivo+".idx");
		indiceIndireto = new ArvoreBMais_String_Int(10,this.diretorio+"/arvoreB."+nomeArquivo+".idx");
	}//end of CRUD constructor
	

	//Método responsável pela criação de um novo item no registro
	public int create(T entidade){
		int sucesso = -1;
		int id = -1;
		long pos = -1;
		try{
			//Primeiro é lido qual o id que deve ser armazenado no cabeçalho do arquivo.
			arquivo.seek(0);
			id = arquivo.readInt();
			//O id é setado no objeto
			entidade.setID(id);
			//O arranjo de bytes a ser armazenado no arquivo é obtido.
         	byte[] data  =  entidade.toByteArray();
			int tamanho  =  data.length;
			//É verificado a existência de lixo dentro do arquivo, aonde o espaço possa ser reciclado.
			pos = lixo.search_empty_space((long)tamanho);
			if(pos == -1)
				pos = arquivo.length();
			//Os dados são escritos na posição encontrada pelos algoritmos anteriores.
			arquivo.seek(pos);
			arquivo.writeChar(' ');
			arquivo.writeInt(data.length);
			arquivo.write(data);
			//Os dados são escritos nos indices dos arquivos
			indiceDireto.create(id, pos);
			indiceIndireto.create(entidade.chaveSecundaria(),id);
			//O id é atualizado e retornado para o usuario
			arquivo.seek(0);
			sucesso = id;
			arquivo.writeInt(id+1);
		} catch(Exception e) {
			sucesso = -1;
		}//end of catch
		return sucesso;
	}//end of create
	
	//Metódo de leitura de dados no arquivo atraves de um id
	public T read(int id){
		T entidade = null;
		try{
			//A posição é verificada dentro do indice
			long pos = indiceDireto.read(id);
			if(pos == -1)
				throw new Exception("Não foi possível encontrar o item desejado");
			//Se por algum motivo o registro já foi deletado, o registro é invalido
			arquivo.seek(pos);
			char deleted = arquivo.readChar();
			if(deleted == '*')
				throw new Exception("Registro inválido");

			//Se o tamanho do registro for diferente do que foi lido dentro do arquivo existe um inconssistência.
			int size = arquivo.readInt();
			byte[] data = new byte[size];
			if(size != arquivo.read(data))
				throw new Exception("Inconssistência nos dados lidos");

			entidade = this.construtor.newInstance();
			entidade.fromByteArray(data);
		} catch(Exception e){
			System.out.print("Deu ruim meu parceiro, por causa da leitura: ");
         e.printStackTrace();
		}//end of catch
		return entidade;
	}//end of read

	//Dentro do indice é verificado a chave secundaria pegando a posição e chamando o metodo de leitura.
	public T read(String chaveSecundaria){
		T entidade = null;
		int id = -1;
		try{
			id = indiceIndireto.read(chaveSecundaria);
		}catch(Exception e){
			System.out.print("Deu ruim meu parceiro, ao ler chave secundaria: ");
         	e.printStackTrace();
		}//end of catch
		if(id != -1)
			entidade = this.read(id);
		return entidade;
	}//end of read
	
	//Método de atualização de entidade
	//O item é deletado e logo após é inserido novamente dentro do arquivo, verificando o tamanho do arquivo
	//e o seu lixo.
	public boolean update(T entidade){
		boolean update = true;
		try{
			long pos_antigo         =  indiceDireto.read(entidade.getID());
			byte[] data             =  entidade.toByteArray();

			// Tamanho em bytes da entidade atualizada
			int tamanho_novo      =  data.length;

			// Tamanho em bytes da entidade antiga, a que ainda esta em disco
			int tamanho_antigo    =  read(entidade.getID()).toByteArray().length; 
			
			// Se o tamanho da entidade atualziada ja for maior que <minimo_sobreescrita>% do tamanho antigo, 
			// eh desnecessario fazer uma busca no indice de lapides e acessar o disco atoa. 
			// Essa busca so vai ser feita se cair no else
			if(( 100*tamanho_novo/tamanho_antigo >= minimo_sobreescrita) && (tamanho_novo <= tamanho_antigo) )
			{
				arquivo.seek(pos_antigo + 2);
				arquivo.writeInt(data.length);
				arquivo.write(data);
			}
			else
			{
				arquivo.seek(pos_antigo);
				arquivo.writeChar('*');

				// retorna se tem uma vaga nesse arquivo de dados
				// que gere <minimo_sobreescrita>% de ocupacao. -1 significa que nao tem
				long pos = lixo.search_empty_space((long)tamanho_novo);
				if(pos == -1)
				{
					// Como nao tem espacos livres, gravar no final do arquivo de dados
					// e criar uma entrada no indice de lapides
					pos = arquivo.length();
					lixo.create_entry_grave(tamanho_antigo, pos_antigo);
				}

				arquivo.seek(pos);

				// Tamanho em bytes da entidade
				arquivo.writeChar(' ');
				arquivo.writeInt(tamanho_novo);
				arquivo.write(data);

				indiceDireto.update(entidade.getID(), pos);
				indiceIndireto.update(entidade.chaveSecundaria(), entidade.getID());
			}
		} catch(Exception e){
			System.out.println("Erro ao atualizar usuario, por esse motivo: ");
			update = false;
			e.printStackTrace();
		}//end of catch
		return update;
	}//end of update

	//Método de delete de registro
	public boolean delete(int id){
		boolean delete = true;
		try{
			if(id < 0)
				throw new Exception("ID inválido. ");
			//Primeiro é verificado a posição
			long pos = indiceDireto.read(id);
			if(pos == -1)
				throw new Exception("ID não existe na base de dados.");
			//O registro é deletado dentro dos indices e a lapide é marcada.
			indiceIndireto.delete(this.read(id).chaveSecundaria());
			indiceDireto.delete(id);
			arquivo.seek(pos);
			arquivo.writeChar('*');
			int tamanho = arquivo.readInt();
			lixo.create_entry_grave((long)tamanho, pos);
		} catch(Exception e){
			delete = false;
			System.out.print("Deu ruim, meu parceiro, ao deletar: ");
			e.printStackTrace();
		}//end of catch
		return delete;
	}//end of delete
}//end of class CRUD
