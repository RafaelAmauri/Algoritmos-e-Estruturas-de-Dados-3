package src;

import utils.utils;

import java.io.RandomAccessFile;


public class ArquivoSequencial
{
    private String save_location;  // path para esse arquivo sequencial
    private RandomAccessFile file; // ponteiro para o arquivo
    

    public ArquivoSequencial(String save_location)
    {
        this.save_location = save_location;
    }


    // armazena no arquivo o par <str> <number>
    public void create(long number, String str)
    {
        try
        {
            this.file = utils.open_file(this.save_location);
            
            file.seek(file.length());

            file.writeUTF(str);
            file.writeLong(number);

            utils.close_file(this.file);
        }
        catch(Exception error)
        {
            error.printStackTrace();
            System.exit(1);
        }
    }


    // retorna o numero que é o par armazenada para <searched_str>
    public long read(String searched_str)
    {
        long paired_long = -1; // long que age como par para string buscada
        boolean flag = true;
        
        try
        {
            this.file = utils.open_file(this.save_location);

            // enquanto nao chegar no fim do arquivo ou nao tiver achado
            // a string, continuar percorrendo 
            while( (flag) && (file.getFilePointer() != file.length()) )
            {
                // se a string do arquivo for igual a string procurada, retornar
                // o endereco de onde ela esta na lista invertida
                if( searched_str.equalsIgnoreCase(this.file.readUTF()) )
                {
                    paired_long = this.file.readLong();
                    flag = false;
                }
                // se nao achou <searched_str>, pular o long que representa o endereco
                else
                    this.file.seek(this.file.getFilePointer() + 8);
            }
            utils.close_file(this.file);
        }
        catch(Exception error)
        {
            error.printStackTrace();
            System.exit(1);
        }

        return paired_long;
    }


    // retorna a string que é o par armazenado para <searched_number>
    public String read(int searched_number)
    {
        String paired_str = "";
        boolean flag = true;

        try
        {
            this.file = utils.open_file(this.save_location);

            // enquanto nao tiver achado <searched_number> ou chegar no
            // final do arquivo, continuar percorrendo
            while( (flag) && (file.getFilePointer() != file.length()) )
            {
                paired_str = this.file.readUTF();

                // se tiver achado o valor, mudar flag para <false>
                if(this.file.readLong() == Integer.toUnsignedLong(searched_number))
                    flag = false;
            }

            // se flag for false, eh pq o valor <searched_number> nao foi achado.
            // por causa disso, <paired_str> deve voltar a ser vazia
            if(flag)
                paired_str = "";

            utils.close_file(this.file);
        }
        catch(Exception error)
        {
            error.printStackTrace();
            System.exit(1);
        }

        return paired_str;
    }


    // usar apenas para debug
    // printa na tela os pares correspondentes <String> <ID>
    public void status()
    {
        try
        {
            this.file = utils.open_file(this.save_location);

            while(file.getFilePointer() != this.file.length())
                System.out.println("Nome = " + this.file.readUTF() + " - ID = " + file.readLong());

            
            utils.close_file(this.file);
        }
        catch(Exception error)
        {
            error.printStackTrace();
            System.exit(1);
        }
    }
}