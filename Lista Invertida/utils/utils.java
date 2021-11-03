package utils;

import java.text.Normalizer;
import java.io.RandomAccessFile;


// Colecao de funcoes que nao sao especificas a uma unica classe 
// e que podem ser usadas por todas
public class utils
{
    // limpa uma string de caracteres especiais
    public static String clear_string(String str)
    {
        String cleared_str = Normalizer.normalize(str, Normalizer.Form.NFD);
        cleared_str = cleared_str.replaceAll("[^\\p{ASCII}]", "");

        return cleared_str;
    }


    // recebe um caminho para arquivo e retorna um RandomAccessFile com permissoes para rw
    public static RandomAccessFile open_file(String filepath) throws Exception
    {
        try
        {
            RandomAccessFile file = new RandomAccessFile(filepath, "rw");
            return file;
        }
        catch(Exception error)
        {
            error.printStackTrace();
            throw error;
        }
    }


    // recebe um ponteiro para RandomAccessFile e fecha o arquivo
    public static void close_file(RandomAccessFile file) throws Exception
    {
        try
        {
            file.close();
        }
        catch(Exception error)
        {
            error.printStackTrace();
            throw error;
        }
    }
}