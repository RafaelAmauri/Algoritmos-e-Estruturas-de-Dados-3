package src;

import utils.utils;

import java.io.RandomAccessFile;
import java.util.ArrayList;


public class ListaInvertida
{
    private String save_location;           // path para o arquivo dessa lista invertida
    private RandomAccessFile reverse_list;  // ponteiro para essa lista invertida
    private ArquivoSequencial index;        // arquivo sequencial com os termos e enderecos. É um indice indireto
    private ArquivoSequencial database;     // arquivo com dados de usuarios. Armazena <ID> <String>
    private int last_id;                    // int representando o ultimo id que foi inserido
    private int OVERFLOW_LIMIT = 10;        // quantidade maxima de IDs que pode ser armazenada em sequencia
    private String[] stop_words = {"da", "de", "do", "das", "dos"}; // stop words


    public ListaInvertida(String save_file)
    {
        this.save_location  =  save_file + ".idx.direto";
        this.index          =  new ArquivoSequencial(save_file + ".idx.indireto");
        this.database       =  new ArquivoSequencial(save_file + ".db");

        try
        {
            this.reverse_list = utils.open_file(this.save_location);

            // verifica se o arquivo esta vazio (length == 0).
            // Se estiver, escreve o ultimo ID como sendo 1. 
            if(this.reverse_list.length() == 0)
            {
                this.reverse_list.writeInt(1);
                this.last_id = 1;
            }
            // Caso contrario, le o ultimo ID inserido 
            else
                this.last_id = this.reverse_list.readInt();

            utils.close_file(this.reverse_list);
        }
        catch(Exception error)
        {
            error.printStackTrace();
            System.exit(1);
        }
    }


    // Insere <str> na lista invertida, indice e arquivo de dados.
    // Caso <str> ja exista, ira ser reinserido, pois pessoas/items diferentes podem
    // ter o mesmo nome. A unica parte que nao sera inserida sao termos que
    // estao em this.stop_words
    public void create(String str)
    {
        String cleared_str  =  utils.clear_string(str); // limpando string de acentos, caracteres especiais...
        
        try
        {
            this.reverse_list = utils.open_file(this.save_location);

            // para cada string <s> no array apos o split(" ")
            for(String s: cleared_str.split(" "))
            {
                // pular se <s> esta no array de stop words
                if(this.is_in_stop_words(s))
                    continue;

                long pos_insertion_reverse_list = this.index.read(s);
                int quant_ids; // armazena a quantidade de IDs ja armazenados naquela posicao da lista

                // Se for -1, eh pq o termo <s> **NAO** existe no indice de termos.
                // Por causa disso, vamos criar ele no indice e depois inserir na lista invertida
                if(pos_insertion_reverse_list == -1)
                {
                    // criando entrada no indice de termos
                    this.index.create(this.reverse_list.length(), s);

                    // pulando para final da lista invertida
                    this.reverse_list.seek(this.reverse_list.length());

                    this.reverse_list.writeInt(1); // so existe 1 ID inserido na lista

                    // inserindo o ID do usuario criado
                    this.reverse_list.writeInt(last_id);

                    // pulando as casas dos outros ints para inserir -1 no ponteiro para proximo
                    this.reverse_list.seek( this.reverse_list.getFilePointer() + ((Integer.SIZE/8)*(this.OVERFLOW_LIMIT-1)));
                    this.reverse_list.writeLong(-1);
                }

                // se nao retornar -1, pular para a posicao retornada pelo indice de termos
                else
                {
                    this.reverse_list.seek(pos_insertion_reverse_list);
                    quant_ids = this.reverse_list.readInt();

                    // se for igual a <OVERFLOW_LIMIT>, eh pq essa seção vai
                    // estourar nessa insercao (pointer_next == -1), ou
                    // ja estourou (pointer_next != -1)
                    if(quant_ids == this.OVERFLOW_LIMIT)
                    {
                        boolean flag_overflow = true;
                        long pointer_next;

                        // enquanto nao achar uma secao com menos de <OVERFLOW_LIMIT> inseridos (ainda nao cheia)
                        // ou uma com <OVERFLOW_LIMIT> inseridos e -1 em pointer_next (cheia, mas onde pointer_next
                        // ainda nao aponta para outra secao)
                        while(flag_overflow)
                        {
                            // pular os ids
                            this.reverse_list.seek( this.reverse_list.getFilePointer() + ((Integer.SIZE/8)*(this.OVERFLOW_LIMIT)));
                            pointer_next = this.reverse_list.getFilePointer();

                            // se pointer_next for igual a -1, deve ser criada uma nova secao no fim do arquivo
                            if(this.reverse_list.readLong() == -1)
                            {
                                // reescrevendo o ponteiro para proximo com final do arquivo
                                this.reverse_list.seek(pointer_next);
                                this.reverse_list.writeLong(this.reverse_list.length());
                                
                                // pular para final do arquivo para fazer insercao
                                this.reverse_list.seek(this.reverse_list.length());
                                pos_insertion_reverse_list = this.reverse_list.getFilePointer();
                                quant_ids = 0;

                                // pulando o int de quantidade e os IDs para inserir -1 no ponteiro para proximo
                                this.reverse_list.seek( this.reverse_list.getFilePointer() + ((Integer.SIZE/8)*(this.OVERFLOW_LIMIT+1)));
                                this.reverse_list.writeLong(-1);

                                flag_overflow = false;
                            }
                            // se pointer_next for != -1, ir para a posicao que ele esta apontando
                            else
                            {
                                this.reverse_list.seek(pointer_next);
                                this.reverse_list.seek(this.reverse_list.readLong());

                                pos_insertion_reverse_list = this.reverse_list.getFilePointer();
                                quant_ids = this.reverse_list.readInt();

                                if(quant_ids != this.OVERFLOW_LIMIT)
                                    flag_overflow = false;
                            }
                        }
                    }
                    // reescrever a quantidade de IDs inseridos, pois vai ser inserido outro
                    this.reverse_list.seek(pos_insertion_reverse_list);
                    this.reverse_list.writeInt(quant_ids+1);
                    
                    // pular <quant> ints para fazer a nova insercao no espaco vazio
                    this.reverse_list.seek(this.reverse_list.getFilePointer() + ((Integer.SIZE/8)*quant_ids));
                    this.reverse_list.writeInt(this.last_id);
                }
            }
            utils.close_file(this.reverse_list);
        }
        catch(Exception error)
        {
            error.printStackTrace();
            System.exit(1);
        }

        // escrevendo o par <ID> <Str> no arquivo de dados
        this.database.create((long)this.last_id, str);

        // reescrevendo o ultimo id inserido
        this.rewrite_last_id();
    }
    

    // Recebe uma string e procura se ela existe nessa lista invertida.
    private int[] _read(String search_str)
    {
        int quant_ids;

        String[] array_terms          =  utils.clear_string(search_str).split(" "); // limpando string
        ArrayList<Integer> array_ids  =  new ArrayList<Integer>();
        ArrayList<Integer> aux_array  =  new ArrayList<Integer>();

        // para intersecao, array_ids vai conter todos IDs possiveis
        for(int i = 0; i < this.last_id; i++)
            array_ids.add(i);

        try
        {
            this.reverse_list = utils.open_file(this.save_location);

            // para cada termo em array_terms, procurar o termo
            // no indice
            for(String s: array_terms)
            {
                // pulando iteracao se <s> for uma stop word
                if(this.is_in_stop_words(s))
                    continue;

                long position = this.index.read(s);

                // se position != -1, eh pq essa string <s> existe no indice
                if(position != -1)
                {
                    this.reverse_list.seek(position);

                    quant_ids  =  this.reverse_list.readInt();
                    // adicionando as IDs da secao em aux_array
                    for(int i = 0; i < quant_ids; i++)
                    {
                        aux_array.add(this.reverse_list.readInt());

                        // se quant_ids for igual ao limite maximo, verificar 
                        // se existe algo na secao <proximo> e ler ela
                        if((i == quant_ids-1) && (quant_ids == this.OVERFLOW_LIMIT))
                        {
                            position = this.reverse_list.readLong();
                            if(position != -1)
                            {
                                i = -1; // no final da iteracao, vai virar 0 e reiniciar o for
                                this.reverse_list.seek(position);
                                quant_ids = this.reverse_list.readInt();
                            }
                        }
                    }
                }
                // intersecao entre <array_ids> e <aux_array>
                array_ids.retainAll(aux_array);
                aux_array.clear();
            }

            utils.close_file(this.reverse_list);
        }
        catch(Exception error)
        {
            error.printStackTrace();
            System.exit(1);
        }

        // converte o ArrayList para uma stream;
        // mapeia cada <i> para si mesmo, no tipo primitivo int;
        // converte tudo para um array de ints
        return array_ids.stream().mapToInt(i -> i).toArray();
    }


    // recebe um array de strings. 
    // Essa funcao vai concatenar as strings do array e passar para a funcao read(String str)
    public int[] read(String[] str_array)
    {
        String bundled = "";
        for(String s: str_array)
        {
            s = s.replace(" ", "");
            bundled += (s + " ");
        }

        int[] array_ids = this._read(bundled);

        return array_ids;
    }


    // abre <this.database> e retorna um array contendo as strings associadas a cada ID do array
    public String[] get_paired_strings(int[] array_ids)
    {
        ArrayList<String> aux_arraylist = new ArrayList<String>();
        String aux_str;

        for(int i: array_ids)
        {
            aux_str = this.database.read(i);
            // se string nao for vazia, adicionar ao arraylist
            if(!aux_str.equals(""))
                aux_arraylist.add(this.database.read(i));
        }

        String[] str_array = new String[aux_arraylist.size()];
        
        return aux_arraylist.toArray(str_array);
    }


    // usar apenas para debug.
    // Printa os valores inseridos na lista
    public void status()
    {
        try
        {
            this.reverse_list = utils.open_file(this.save_location);

            int quant_ids, id;
            long next;

            // se a lista nao estiver vazia, printar o primeiro int dela.
            // O primeiro int se refere ao last_id
            if(this.reverse_list.getFilePointer() != this.reverse_list.length())
                System.out.println(this.reverse_list.readInt());

            // enquanto nao chegar no fim da lista, printar
            // <quantidade_ids> <lista dos ids> <proximo>
            while(this.reverse_list.getFilePointer() != this.reverse_list.length())
            {
                quant_ids = this.reverse_list.readInt();
                System.out.printf("%d ", quant_ids);

                for(int i = 0; i < quant_ids; i++)
                {
                    id = this.reverse_list.readInt();
                    System.out.printf("%d ", id);
                }

                this.reverse_list.seek(this.reverse_list.getFilePointer() + ((Integer.SIZE/8)*(this.OVERFLOW_LIMIT-quant_ids)));
                next = this.reverse_list.readLong();

                System.out.println(next);
            }
            utils.close_file(this.reverse_list);
        }
        catch(Exception error)
        {
            error.printStackTrace();
            System.exit(1);
        }
    }


    // O primeiro int do arquivo da lista invertida eh o ultimo ID inserido
    private void rewrite_last_id()
    {
        try
        {
            this.reverse_list = utils.open_file(this.save_location);
            
            this.reverse_list.writeInt(++this.last_id);

            utils.close_file(reverse_list);
        }
        catch(Exception error)
        {
            error.printStackTrace();
            System.exit(1);
        }

    }


    // retorna booleano indicando se <str> esta na lista de stop words
    private boolean is_in_stop_words(String str)
    {
        boolean contained = false;

        for(String s: this.stop_words)
            if(s.equalsIgnoreCase(str))
            {
                contained = true;
                break;
            }

        return contained;
    }    
}