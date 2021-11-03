import src.ListaInvertida;


public class Principal
{
    public static void main(String[] args)
    {
        String save_file = "./dados_usuarios";
        ListaInvertida l = new ListaInvertida(save_file);

        l.create("Marcos Antônio de Oliveira");
        l.create("José Marcos Resende");
        l.create("Paula Oliveira");
        l.create("Carlos José Antônio Souza");
        l.create("José Carlos de Paula");        

        // teste com as strings "paula" e "jose"
        String[] a = {"paula", "jose"};
        int array_ids[] = l.read(a);
    
        String[] paired_strings = l.get_paired_strings(array_ids);
        for(String s: paired_strings)
            System.out.println(s);
    }
}