import java.io.*;

class Movie
{
	String name, director;
	int box_office;
	short release_year;

	
	Movie(String name, String director, int box_office, short release_year)
	{
		this.name          =  name;
		this.director      =  director;
		this.box_office    =  box_office;
		this.release_year  =  release_year;
	}

	
	Movie()
	{
		this.name          =  "";
        this.director      =  "";
        this.box_office    =  0;
        this.release_year  =  0;

	}


	// Printa na tela as informacoes do filme
	void pretty_print()
	{
		String str = ("Name..........: " + this.name         + "\n" + 
					  "Director......: " + this.director     + "\n" +
					  "Box office....: " + this.box_office   + "\n" +
					  "Release year..: " + this.release_year + "\n"  
				     );

	
		System.out.println(str);
	}

	
	// Retorna o tamanho do filme, em bytes
	short size()
	{
		short size = (short)(  (this.name.getBytes().length)     + 2 + // 2 bytes for measuring the UTF8 string 
				               (this.director.getBytes().length) + 2 +
					           ( (Integer.SIZE + Short.SIZE) / Byte.SIZE) 
                            );

		return size;
	}


	/*
	 * Metodo para escrever o filme em um arquivo de disco. Recebe um filepath
	 * completo e um valor para overwrite, caso queira sobreescrever um arquivo que ja existe.
     * Ao usar o overwrite, garanta que nao vao sobrar bytes extras(lixo) do filme anteriormente gravado!!
	 */
	void write_object(String filepath, boolean overwrite)
	{
		try
		{
			RandomAccessFile file = new RandomAccessFile(filepath, "rw");
			
			if(!overwrite)
			{
				file.seek(file.length());
			}
				
			// Writing whole object size in bytes
			short size = this.size();

			System.out.println("INFO: The object has " + size + " bytes");
			 
			// Writing the object size for possible skipping when reading file	
			file.writeShort(size);
			
			// Writing object info
			file.writeUTF(this.name);
			file.writeUTF(this.director);
			file.writeInt(this.box_office);
			file.writeShort(this.release_year);
			
			file.close();
		}
		catch(IOException error)
		{
			System.out.println(error);
		}
	}
	

	/*  Recebe um filepath completo para o local do arquivo 
	 *  gravado e uma posicao dentro dele, para saber quantos pular.
	 *  Exemplo: Se quero ler o segundo filme do arquivo, pulo uma posicao. 
	 *  Logo, chamo a funcao com position=1
	 */
	void read_object(String filepath, int position)
	{
		try
		{
			RandomAccessFile file = new RandomAccessFile(filepath, "r");
				
			short obj_size;
			
			for(int i = 0; i < position; i++)
			{
				obj_size = file.readShort();
				
				file.seek(obj_size + file.getFilePointer());
			}
			
			obj_size = file.readShort();
			
			this.name = file.readUTF();
			this.director = file.readUTF();
			this.box_office = file.readInt();
			this.release_year = file.readShort();

			file.close();
		}
		catch(IOException error)
		{
			System.out.println(error);
		}

	}
}


public class aed3_projeto
{
	public static void main(String[] args)
	{
		Movie m1 = new Movie("Memento", "Christopher Nolan", 39700000, (short)2000);
		m1.write_object("./meu_arq.dat", true);
		//m1.read_object("./meu_arq.dat", 0);		
		//m1.pretty_print();		


		Movie m2 = new Movie("Clube da Luta", "David Fincher", 100900000, (short)(1999));
		m2.write_object("./meu_arq.dat", false);
		//m2.read_object("./meu_arq.dat", 1);
		//m2.pretty_print();


		Movie m3 = new Movie("O Poderoso Chefão", "Francis Copolla", 136800000, (short)1972);
		m3.write_object("./meu_arq.dat", false);
		//m3.read_object("./meu_arq.dat", 2);
		//m3.pretty_print();


		Movie m4 = new Movie("Star Wars IV", "George Lucas", 775500000, (short)1977);
		m4.write_object("./meu_arq.dat", false);
		//m4.read_object("./meu_arq.dat", 3);
		//m4.pretty_print();
		

		Movie m5 = new Movie("O Farol", "Robert Eggers", 14900000, (short)2019);
		m5.write_object("./meu_arq.dat", false);
		//m5.read_object("./meu_arq.dat", 1);			
		//m5.pretty_print();	

		Movie m6 = new Movie();
		//m6.read_object("./meu_arq.dat", 2);
		//m6.pretty_print();
	}
}
