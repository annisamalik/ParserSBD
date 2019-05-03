/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parsersbd;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**
 *
 * @author Asus
 */
public class ParserSBD {
    public static void main(String[] args)  {
        
        Scanner scan= new Scanner(System.in);
        //load data menjadi objek tabel
        List<Tabel> file = bukaFile("database.csv");
        
        //menampilkan menu
        TampilMenu();
        
        //akan terus menampilkan menu selama pilihan tidak 0
        int input = scan.nextInt();
        while (input!=0){
            ProsesMenu(input, fetchPorB ("database.csv", "p"), fetchPorB ("database.csv", "b"), file);
            System.out.println("");
            TampilMenu();
            input =scan.nextInt();
            System.out.flush(); 
        }
    }
    
    //membuat tabel baru sesuai dengan query, karena tidak semua query memilih semua kolom
    private static List<Tabel> InputTabeldariQuery(List<String> kata2query, List<Tabel> file){
        List<Tabel> tabel = new ArrayList<>();
        boolean ada = false;
        int  g =0;
        Tabel ini;
        //memasukkan tabel
        while (g<kata2query.size() && !kata2query.get(g).equals("using")){
            for (int m=0; m<file.size();m++){
                if (kata2query.get(g).equals(file.get(m).getNamatabel())){
                    ini = new Tabel(file.get(m).getNamatabel(), new ArrayList<String>(), file.get(m).getRekord(), file.get(m).getJmlData(), file.get(m).getVdata(), file.get(m).getPrimarykey());
                    tabel.add(ini); 
                    ini.setJmlblok(file.get(m).getJmlblok());
                }
            }
            g++;
        }
        if (!kata2query.contains("*")){
            int i = 0;
            while (i<kata2query.size() && !kata2query.get(i).contains("from")){
                for (int j = 0; j<file.size();j++){
                    for (int k=0; k<file.get(j).getListNamakolom().size();k++){
                        if (kata2query.get(i).equals(file.get(j).getNamakolom(k))){
                            int y = 0;
                            while (y<tabel.size() && !tabel.get(y).getNamatabel().equals(file.get(j).getNamatabel())){
                                y++;
                            }
                            if (y<tabel.size()){
                                if (tabel.get(y).getNamatabel().equals(file.get(j).getNamatabel()))
                                tabel.get(y).tambahkolom(kata2query.get(i));
                            } 
                        }
                    }
                }
                i++;
            }
        }else{
            for (int i=0; i<tabel.size();i++){
                for(Tabel t : file){
                    if(tabel.get(i).getNamatabel().equals(t.getNamatabel())){
                        tabel.get(i).setNamakolom(t.getListNamakolom());
                    }
                }
            }
        }
        return tabel;
    }
    
    //menampilkan tabel, bisa buat tabel aja bebas
    private static void TampilkanTabeldariQuery(List<Tabel> tabel){
        System.out.print("Tabel :");
        for (int j = 0; j<tabel.size(); j++){
            System.out.print(tabel.get(j).getNamatabel()+", ");
        }
        System.out.println("");
        System.out.print("Kolom : ");
        for (int j = 0; j<tabel.size(); j++){
           tabel.get(j).getListKolom();
           System.out.print(", ");
        }
    }
    
    //menampung semua data csv menjadi tabel
    private static List<Tabel> bukaFile(String string) {
       String line = null;
       List<Tabel> tbl = new ArrayList<>();
       int i = 0;
       try {
           FileReader fileReader = new FileReader(string);
           BufferedReader bufferedReader = new BufferedReader(fileReader);
           while ((line = bufferedReader.readLine()) != null) {
               List<String> col = tokenizer(line, ',');
               String nama = col.get(0);
               col.remove(0);
               int R = 0;
               int n = 0;
               int V = 0;
               for (int j = 0; j<col.size(); j++){

                 if (col.get(j).equals("r")){
                     R = Integer.parseInt(col.get(j+1));
                     col.remove(j+1);
                     col.remove(j);
                     j--;
                 }else if(col.get(j).equals("n")){
                     n = Integer.parseInt(col.get(j+1));
                     col.remove(j+1);
                     col.remove(j);
                     j--;
                 }else if(col.get(j).equals("v")){
                     V = Integer.parseInt(col.get(j+1));
                     col.remove(j+1);
                     col.remove(j);
                     j--;
                 }
               }
               if(nama.length()>1) {
                   Tabel yuhu = new Tabel(nama, col, R, n, V);
                   yuhu.setJmlblok((int)jumlahBlok(yuhu.getJmlData(), fetchPorB(string, "b")));
                   tbl.add(yuhu);
               }
           }
           bufferedReader.close();
       } catch (FileNotFoundException ex) {
           System.out.println(
                   "Unable to open file '"
                   + string + "'");
       } catch (IOException ex) {
           System.out.println(
                   "Error reading file '"
                   + string + "'");
       }
       return tbl;  
   }

    //mengambil data pointer dan ukuran blok
    private static int fetchPorB (String input, String pilih){
        String line = null;
        int data = 0;
        int i = 0;
        try {
            FileReader fileReader = new FileReader(input);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            if ((line = bufferedReader.readLine()) != null){
                List<String> col = tokenizer(line, ',');
                for (int j = 0; j<col.size(); j++){
                    if (col.get(j).equals("p") && pilih.equals("p")){
                          data = Integer.parseInt(col.get(j+1));
                      }else if(col.get(j).equals("b") && pilih.equals("b")){
                          data = Integer.parseInt(col.get(j+1));
                      }
                }
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '"
                    + input + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                    + input + "'");
        }
        return data;
    }
    
    //memisahkan kalimat menjadi list of string
    private static List<String> tokenizer(String query, char div) {
       List<String> al = new ArrayList<>();
       String token = "";
       for (char ch : query.toCharArray()) {
           if (ch == div) {
               if (!token.equals("")) al.add(token.toLowerCase());
               token = "";
           }else if (ch == ';'){
               if (!token.equals("")) al.add(token.toLowerCase());
               al.add(";");
               token = "";
           }else if (ch == ',' || ch =='.' || ch =='(' || ch== ')' || ch == '='){
               if (!token.equals("")) al.add(token.toLowerCase());
               if (ch == '.')
                   al.add(".");
               else if (ch == ',')
                   al.add(",");  
               else if (ch == '(')
                   al.add("("); 
               else if (ch == ')')
                   al.add(")"); 
               else if (ch == '=')
                   al.add("="); 
               token = "";
           }else if (ch != div){
               token += ch;
           }
       }
       if (!token.equals("")) al.add(token.toLowerCase());
       return al;
   }    
    
    //PDA khusus select biasa
    private static PDA PDAselectBiasa(List<Tabel> file, List<String> query){
        int statetabelstart, statetabelend, kolomstart, kolomend, kolomkoma, kolomkomaend, tabelulang, tabelulangend, kondisi, kondisiend;
        int syarat, syaratend, operator, operatorend, bacainput, bacainputend, tambahkondisi, tambahkondisiend;
        PDA parser = new PDA();
        parser.tambahTransisi("q1", "select", "", "select", "q2");
        parser.tambahTransisi("q2", "*", "select", "*", "q3");
        parser.tambahTransisi("q3", "from", "*", "from", "q4");
        statetabelstart = 5;
        for(int i = 0; i< file.size();i++){
            parser.tambahTransisi("q4", file.get(i).getNamatabel(), "from", file.get(i).getNamatabel(), "q"+statetabelstart);
            statetabelstart++;
        }
        statetabelend = statetabelstart-1;
        statetabelstart = 5;
        
        for (int i = 0; i< file.size();i++){
            parser.tambahTransisi("q"+statetabelstart, ";", file.get(i).getNamatabel(), ";", "end");
            statetabelstart++;
        }
        statetabelstart = 5;
       
        //kolom tabel
        kolomstart = statetabelend+1;
        for (int i = 0; i < file.size(); i++){
            for (int k = 0; k<file.get(i).getListNamakolom().size() ; k++){
                parser.tambahTransisi("q2", file.get(i).getNamakolom(k), "select", file.get(i).getNamakolom(k), "q"+kolomstart);
            }
            kolomstart++;
        }
        
        kolomend = kolomstart--;
        kolomstart = statetabelend+1;
        
        kolomkoma = kolomend+1;
        for (int i = 0; i< file.size();i++){
            for (int k = 0; k<file.get(i).getListNamakolom().size() ; k++){
                parser.tambahTransisi("q"+kolomstart, ",", file.get(i).getNamakolom(k), ",", "q"+kolomkoma);
            }
            kolomstart++;
            kolomkoma++;
        }
        kolomkomaend = kolomkoma--;
        kolomstart = statetabelend+1;
        kolomkoma = kolomend+1;
        
        
        for (int i = 0; i< file.size();i++){
            for (int k = 0; k<file.get(i).getListNamakolom().size() ; k++){
                parser.tambahTransisi("q"+kolomkoma, file.get(i).getNamakolom(k), ",", file.get(i).getNamakolom(k), "q"+kolomstart);
            }
            kolomstart++;
            kolomkoma++;
        }
        kolomstart = statetabelend+1;
        kolomkoma = kolomend+1;
        
        tabelulang = kolomkomaend+1;
        for (int i = 0; i< file.size();i++){
            for (int k = 0; k<file.get(i).getListNamakolom().size() ; k++){
                parser.tambahTransisi("q"+kolomstart, "from", file.get(i).getNamakolom(k), "from", "q"+tabelulang);        
            }
            kolomstart++;
            tabelulang++;
        }
        tabelulangend = tabelulang--;
        kolomstart = statetabelend+1;
        tabelulang = kolomkomaend+1;

        for (int i = 0; i< file.size();i++){
            parser.tambahTransisi("q"+tabelulang, file.get(i).getNamatabel(), "from", file.get(i).getNamatabel(), "q"+statetabelstart);
            statetabelstart++;
            tabelulang++;
        }
        tabelulang = kolomkomaend+1;
        statetabelstart = 5;
        
        //kondisi where
        kondisi = tabelulangend+1;
        for (int i = 0; i< file.size();i++){
            parser.tambahTransisi("q"+statetabelstart, "where", file.get(i).getNamatabel(), "where", "q"+kondisi);
            statetabelstart++;
            kondisi++;
        }
        statetabelstart = 5;
        kondisiend = kondisi--;
        kondisi = tabelulangend+1;
        
        syarat = kondisiend+1;
        for (int i = 0; i< file.size();i++){
            for (int k = 0; k < file.get(i).getListNamakolom().size(); k++){
                parser.tambahTransisi("q"+kondisi, file.get(i).getNamakolom(k), "where", file.get(i).getNamakolom(k), "q"+syarat);
            }
            syarat++;
            kondisi++;
        }
        syaratend = syarat--;
        kondisi = tabelulangend+1;
        syarat = kondisiend+1;
        
        operator = syaratend+1;
        for (int i = 0; i< file.size();i++){
            for (int k = 0; k < file.get(i).getListNamakolom().size(); k++){
                parser.tambahTransisi("q"+syarat, "=", file.get(i).getNamakolom(k), "=", "q"+operator);
                parser.tambahTransisi("q"+syarat, ">", file.get(i).getNamakolom(k), ">", "q"+operator);
                parser.tambahTransisi("q"+syarat, "<", file.get(i).getNamakolom(k), "<", "q"+operator);
                parser.tambahTransisi("q"+syarat, ">=", file.get(i).getNamakolom(k), ">=", "q"+operator);
                parser.tambahTransisi("q"+syarat, "<=", file.get(i).getNamakolom(k), "<=", "q"+operator);
            }
            syarat++;
            operator++;
        }
        operatorend = operator--;
        operator = syaratend+1;
        syarat = kondisiend+1;
        
        bacainput = operatorend+1;
        for (int i = 0; i< file.size();i++){
            for (int k = 0; k < inputUser(query).size() ; k++){
                parser.tambahTransisi("q"+operator, inputUser(query).get(k), "=", inputUser(query).get(k), "q"+bacainput);
                parser.tambahTransisi("q"+operator, inputUser(query).get(k), ">", inputUser(query).get(k), "q"+bacainput);
                parser.tambahTransisi("q"+operator, inputUser(query).get(k), "<", inputUser(query).get(k), "q"+bacainput);
                parser.tambahTransisi("q"+operator, inputUser(query).get(k), ">=", inputUser(query).get(k), "q"+bacainput);
                parser.tambahTransisi("q"+operator, inputUser(query).get(k), "<=", inputUser(query).get(k), "q"+bacainput);
            }
            bacainput++;
            operator++;
        }
        bacainputend = bacainput--;
        operator = syaratend+1;
        bacainput = operatorend+1;
        
        tambahkondisi = bacainputend+1;
        for (int i = 0; i< file.size();i++){
            for (int k = 0; k < inputUser(query).size() ; k++){
                parser.tambahTransisi("q"+bacainput, "and", inputUser(query).get(k), "and", "q"+tambahkondisi);
                parser.tambahTransisi("q"+bacainput, "or", inputUser(query).get(k), "or", "q"+tambahkondisi);        
            }
            bacainput++;
            tambahkondisi++;
        }
        tambahkondisiend = tambahkondisi--;
        bacainput = operatorend+1;
        tambahkondisi = bacainputend+1;
        
        for (int i = 0; i< file.size();i++){
            for (int k = 0; k <  file.get(i).getListNamakolom().size() ; k++){
                parser.tambahTransisi("q"+tambahkondisi,  file.get(i).getNamakolom(k), "and",  file.get(i).getNamakolom(k), "q"+syarat);
                parser.tambahTransisi("q"+tambahkondisi,  file.get(i).getNamakolom(k), "or",  file.get(i).getNamakolom(k), "q"+syarat);        
            }
            syarat++;
            tambahkondisi++;
        }
        tambahkondisi = bacainputend+1;
        syarat = kondisiend+1;        
        
        //end kondisi
        for (int i = 0; i< file.size();i++){
            for (int k = 0; k < inputUser(query).size() ; k++){
                parser.tambahTransisi("q"+bacainput, ";", inputUser(query).get(k), ";", "end");
            }
            bacainput++;
        }
        bacainput = operatorend+1;
        
        //mengkosongkan stack
        parser.tambahTransisi("end", "", ";", "", "end2"); 
        parser.tambahTransisi("end2", "", "#", "", "f");
        
        return parser;
    }
    
    //PDA khusus join 2 tabel, fleksibel meski data csv diubah
    private static PDA PDAjoin2Tabel(List<Tabel> file){
        
        int statetabelstart, statetabelend, tabeljoin, tabeljoinend, possjoin, possjoinend, stateusing, stateusingend;
        int kurungbuka, kurungbukaend, kurungtutup, kondisisama, kondisisamaend;
        int pilihkolom, pilihulangkolom, pilihkolomend, pilihulangkolomend, ketabel;
        int fromkolom, fromkolomend;
        PDA pjoin = new PDA();
        
        pjoin.tambahTransisi("q1", "select", "", "select", "q2");
        pjoin.tambahTransisi("q2", "*", "select", "*", "q3");
        pjoin.tambahTransisi("q3", "from", "*", "from", "q4");
        statetabelstart = 5;
        
        for(int i = 0; i< file.size();i++){
            pjoin.tambahTransisi("q4", file.get(i).getNamatabel(), "from", file.get(i).getNamatabel(), "q"+statetabelstart);
            statetabelstart++;
        }
        statetabelend = statetabelstart-1;
        statetabelstart = 5;
        
        tabeljoin = statetabelend+1;
        
        for (int i = 0; i< file.size();i++){
            pjoin.tambahTransisi("q"+statetabelstart, "join", file.get(i).getNamatabel(), "join", "q"+tabeljoin);
            tabeljoin++;
            statetabelstart++;
        }
        statetabelstart = 5;
        tabeljoinend = tabeljoin--;
        tabeljoin = statetabelend+1;
        
        possjoin = tabeljoinend+1;
        for (int i = 0; i< file.size();i++){
            for(int k = 0; k <possibleJoin(file.get(i), file).size(); k++){
                pjoin.tambahTransisi("q"+tabeljoin, possibleJoin(file.get(i), file).get(k).getNamatabel(), "join", possibleJoin(file.get(i), file).get(k).getNamatabel(), "q"+possjoin);
                possjoin++;
            }
            tabeljoin++;
        }
        possjoinend = possjoin--;
        tabeljoin = statetabelend+1;
        possjoin = tabeljoinend+1;
        
        stateusing = possjoinend+1;
        for (int i = 0; i< file.size();i++){
            for(int k = 0; k <possibleJoin(file.get(i), file).size(); k++){
                pjoin.tambahTransisi("q"+possjoin, "using", possibleJoin(file.get(i), file).get(k).getNamatabel(), "using", "q"+stateusing);
                possjoin++;
                stateusing++;
            }
        }
        possjoin = tabeljoinend+1;
        stateusingend = stateusing--;
        stateusing = possjoinend+1;
        
        kurungbuka = stateusingend+1;
        for (int i = 0; i< file.size();i++){
            for(int k = 0; k <possibleJoin(file.get(i), file).size(); k++){
                pjoin.tambahTransisi("q"+stateusing, "(", "using", "(", "q"+kurungbuka);
                kurungbuka++;
                stateusing++;
            }
        }
        kurungbukaend = kurungbuka--;
        stateusing = possjoinend+1;
        kurungbuka = stateusingend+1;
        
        kondisisama = kurungbukaend+1;
        for (int i = 0; i< file.size();i++){
            for(int k = 0; k <possibleJoin(file.get(i), file).size(); k++){
                pjoin.tambahTransisi("q"+kurungbuka, sameColoumn(file.get(i), possibleJoin(file.get(i), file).get(k)), "(", sameColoumn(file.get(i), possibleJoin(file.get(i), file).get(k)), "q"+kondisisama);
                kurungbuka++;
                kondisisama++;
            }
        }
        kondisisamaend = kondisisama--;
        kondisisama = kurungbukaend+1;
        kurungbuka = stateusingend+1;
        
        kurungtutup = kondisisamaend+1;
        for (int i = 0; i< file.size();i++){
            for(int k = 0; k <possibleJoin(file.get(i), file).size(); k++){
                pjoin.tambahTransisi("q"+kondisisama, ")", sameColoumn(file.get(i), possibleJoin(file.get(i), file).get(k)), ")", "q"+kurungtutup);
                kondisisama++;
            }
        }
        kondisisama = kurungbukaend+1;
        
        //memilih kolom di join
        pilihkolom = kurungtutup+1;
        
        List<Join> lohjoin = jumlahKombinasi(file);
        for(int i = 0; i<lohjoin.size();i++){
            for(int k = 0; k < lohjoin.get(i).getKolom().size(); k++){
                pjoin.tambahTransisi("q2", lohjoin.get(i).getNamakolom(k), "select", lohjoin.get(i).getNamakolom(k), "q"+pilihkolom);
            }
            pilihkolom++;
        }

        pilihkolomend = pilihkolom--;
        pilihkolom = kurungtutup+1;
        
        pilihulangkolom = pilihkolomend+1;
        for(int i = 0; i<lohjoin.size();i++){
            for(int k = 0; k < lohjoin.get(i).getKolom().size(); k++){
                pjoin.tambahTransisi("q"+pilihkolom, ",", lohjoin.get(i).getNamakolom(k), ",", "q"+pilihulangkolom);
            }
            pilihkolom++;
            pilihulangkolom++;
        }

        pilihkolom = kurungtutup+1;
        pilihulangkolomend = pilihulangkolom--;
        pilihulangkolom = pilihkolomend+1;
        
        
        
        for(int i = 0; i<lohjoin.size();i++){
            for(int k = 0; k < lohjoin.get(i).getKolom().size(); k++){
                pjoin.tambahTransisi("q"+pilihulangkolom, lohjoin.get(i).getNamakolom(k), ",", lohjoin.get(i).getNamakolom(k), "q"+pilihkolom);
            }
            pilihkolom++;
            pilihulangkolom++;
        }
        
        pilihkolom = kurungtutup+1;
        pilihulangkolom = pilihkolomend+1;
        
        fromkolom = pilihulangkolomend+1;
        for(int i = 0; i<lohjoin.size();i++){
            for(int k = 0; k < lohjoin.get(i).getKolom().size(); k++){
                pjoin.tambahTransisi("q"+pilihkolom, "from", lohjoin.get(i).getNamakolom(k), "from", "q"+fromkolom);
            }
            pilihkolom++;
            fromkolom++;
        }
        fromkolomend = fromkolom--;
        pilihkolom = kurungtutup+1;
        fromkolom = pilihulangkolomend+1;
        
        ketabel = fromkolomend+1;
        for(int i = 0; i<lohjoin.size();i++){
            for(int k = 0; k< file.size();k++){
                if (lohjoin.get(i).getTabel().contains(file.get(k).getNamatabel())){
                    pjoin.tambahTransisi("q"+fromkolom, file.get(k).getNamatabel(), "from", file.get(k).getNamatabel(), pjoin.cariState(file.get(k).getNamatabel(), "from"));
                }  
            }
            fromkolom++;
        }

        fromkolom = pilihulangkolomend+1;

        pjoin.tambahTransisi("q"+kurungtutup, ";", ")", ";", "end");
        pjoin.tambahTransisi("end", "", ";", "", "end2");
        pjoin.tambahTransisi("end2", "", "#", "", "f");
        
        return pjoin;
        
    }
    
    //untuk menentukan input user ketika ada kondisi equality/komparison
    private static List<String> inputUser(List<String> input){
        boolean ambil = false;
        String output = "";
        List<String> listInput = new ArrayList<>();
        int j = 0;
        for(int i = 0; i < input.size(); i++){  
            if (ambil && j <= 3){
                j++;
                if (j == 3){
                    listInput.add(input.get(i));
                    ambil = false;
                }
            }
            if(input.get(i).equals("where") || input.get(i).equals("and") || input.get(i).equals("or")){
                j = 0;
                ambil = true;
            }
        }
        return listInput;
    }
    
    //PDAJOIN: mencari kemungkinan tabel untuk di join
    private static List<Tabel> possibleJoin(Tabel t, List<Tabel> tbl){
        List<Tabel> output = new ArrayList<>();
        for (String a: t.getListNamakolom()){
            for(Tabel tab : tbl){
                for(String s : tab.getListNamakolom()){
                    if (!tab.getNamatabel().equals(t.getNamatabel()) && a.equals(s)){
                        output.add(tab);
                    }
                }
            }
        }
        return output;
    }
    
    //PDAJOIN: untuk mencari kolom yang sama antara dua tabel untuk kemungkinan input join
    private static String sameColoumn(Tabel a, Tabel b){
        String output = "";
        for(String c: a.getListNamakolom()){
            for(String d : b.getListNamakolom()){
                if (c.equals(d)){
                    output = c;
                }
            }
        }
        return output;
    }
    
    //PDAJOIN: membuat cabang akan ada berapa join yang input kolom berbeda
    private static List<Join> jumlahKombinasi(List<Tabel> file){
    List<Tabel> tabel = new ArrayList<>();
    tabel.addAll(file);
    List<Join> inijoin = new ArrayList<>();
    List<String> initabel = new ArrayList<>();
    List<String> kolom = new ArrayList<>();
    Join temp;
    for(int i = 0 ; i< tabel.size(); i++){
        for (String a: tabel.get(i).getListNamakolom()){
            for(Tabel tab : tabel){
                for(String s : tab.getListNamakolom()){
                    if (!tab.getNamatabel().equals(tabel.get(i).getNamatabel()) && a.equals(s)){
                        temp = new Join();
                        temp.setKoloms(tabel.get(i).getListNamakolom());
                        temp.setKoloms(tab.getListNamakolom());
                        temp.setTabel(tabel.get(i).getNamatabel());
                        temp.setTabel(tab.getNamatabel());
                        inijoin.add(temp);

                    }
                }
            }
        }
        tabel.remove(i);
        i--;
    }
    return inijoin;
}


    //Menu utama
    private static void TampilMenu(){
        System.out.println("Menu Utama:");
        System.out.println("1. Tampilkan BFR dan Fanout Ratio Setiap tabel");
        System.out.println("2. Tampilkan Total Blok Data + Blok Index Setiap Tabel");
        System.out.println("3. Tampilkan Jumlah Blok yang Diakses Untuk Pencarian Rekord");
        System.out.println("4. Tampilkan QEP dan Cost");
        System.out.println("5. Tampilkan Isi File Shared Pool");
        System.out.println("Masukkan Pilihan Anda...");
    }
    

    
    private static void ProsesMenu(int i, int P, int B, List<Tabel> tbl){
       Scanner scan= new Scanner(System.in);
       Scanner scans= new Scanner(System.in);
       for(Tabel t : tbl){
            int b = (int) jumlahBlok(t.getJmlData(), hitungBFR(B, t.getRekord()));
            t.setJmlblok(b);
            
        }
       switch (i) {
           case 1:
               for(Tabel t : tbl){
                   int bfr = hitungBFR(B, t.getRekord());
                   System.out.println("BFR "+t.getNamatabel()+" : "+ bfr);
                   int y = hitungFanOut(B, t.getVdata(), P);
                   System.out.println("Fan Out Ratio "+t.getNamatabel()+" : "+y);
               }
               break;
           case 2:
               for(Tabel t : tbl){
                   int ix = (int) indexTabel(t.getJmlData(),hitungFanOut(B, t.getVdata(), P));
                   System.out.println("Tabel Data "+t.getNamatabel()+" : "+ t.getJmlblok()+" blok");
                   System.out.println("Indeks "+t.getNamatabel()+" : "+ix+" blok");
               }
               break;
           case 3:
               System.out.println("Input : ");

               System.out.print("Cari Rekord ke- : ");
               int n =scan.nextInt();
               System.out.print("Nama Tabel : ");
               String tabs =scans.nextLine();
               
               if(TabelAda(tabs, tbl)){
                   Tabel t = TabelAdaCari(tabs, tbl);
                   if (n<=t.getJmlData()){
                        System.out.println("Menggunakan Index, jumlah blok yang diakses : "+(int)cariJmlIndex(n, hitungFanOut(B, t.getVdata(), P)));
                        System.out.println("Tanpa indeks, jumlah blok yang diakses : "+(int)cariJmlBlok(n, hitungBFR(B,t.getRekord())));
                   }else{
                   System.out.println("Maaf data ke-"+n+" tidak tersedia.");
                   System.out.println("Pencarian sampai data terakhir adalah sebagai berikut:");
                   System.out.println("Menggunakan Index, jumlah blok yang diakses : "+(int)cariJmlIndex(t.getJmlData(), hitungFanOut(B, t.getVdata(), P)));
                   System.out.println("Tanpa indeks, jumlah blok yang diakses : "+(int)cariJmlBlok(t.getJmlData(), hitungBFR(B,t.getRekord())));
                   }
                }else{
                   System.out.println("Maaf data tabel yang anda input tidak ada.");
               }

               break;
           case 4:
               System.out.println("Input Query: ");
               String query =scan.nextLine();
               List<String> kata2query = tokenizer(query, ' ');
               List<Tabel> tab = InputTabeldariQuery(kata2query, tbl);
               if (kata2query.contains("join")){
                   PDA pjoin = PDAjoin2Tabel(tbl);
                                      
                   System.out.print("Hasil Cek :");
                   System.out.println(pjoin.cek(kata2query));
                   System.out.println("");
                   System.out.println("Output : ");
                   
                   TampilkanTabeldariQuery(tab);
                   System.out.println("");
                   System.out.println("");
                   if(pjoin.valid(kata2query)){
                        int tinggi = (int) tinggiPohon(hitungFanOut(B, tab.get(0).getVdata(), P), tab.get(0).getJmlblok());
                        QEP hasil = new QEP(tab, kata2query, tinggi);
                        hasil.setQueryaja(query);
                        hasil.hasilQEP(tab, kata2query); 
                        String masukdata = hasil.getKombinasilistCost().getQuery()+"\n"+hasil.getKombinasilistCost().getTulisan();
                        tulisData(masukdata);
                   System.out.println("");
                   }
               }else{
                   PDA parser = PDAselectBiasa(tbl, kata2query);
                   System.out.print("Hasil Cek :");
                   System.out.println(parser.cek(kata2query));
                   
                   System.out.println("");
                   System.out.println("Output : ");
                
                   TampilkanTabeldariQuery(tab);
                   System.out.println("");
                   System.out.println("");
                   if(parser.valid(kata2query)){
                       if (kata2query.contains("where")){
                            int tinggi = (int) tinggiPohon(hitungFanOut(B, tab.get(0).getVdata(), P), tab.get(0).getJmlblok());
                            QEP hasil = new QEP(tab, kata2query, tinggi);
                            hasil.setQueryaja(query);
                            hasil.hasilQEP(tab, kata2query); 

                            System.out.println("");
                            String masukdata = hasil.getKombinasilistCost().getQuery()+"\n"+hasil.getKombinasilistCost().getTulisan()+"\n";
                            tulisData(masukdata);
                        }  
                       
                   }
                   System.out.println("");
               }
               
               break;
           case 5:
               System.out.println("Tampilan Data Shared Pool");
               System.out.println("");
               System.out.println(readSharedPool("SharedPool.txt"));
               break;
           default:

               break;
       }
    }


    //method perhitungan numerik
    private static int hitungBFR(int B, int R){
        return (B/R);
    }

    private static int hitungFanOut(int B, int V, int P){
        return (B/(V+P));
    }

    private static double jumlahBlok(int n, int bfr){
        return Math.ceil(((double)n/(double)bfr));
    }
    
    private static double tinggiPohon(int y, int b){
        return Math.ceil(Math.log((double)b)/Math.log((double)y));
    }

    private static double indexTabel(int n, int y){
        return Math.ceil(((double)n/(double)y));
    }

    private static double cariJmlBlok(int n, int bfr){
        return Math.ceil(((double)n/(double)bfr));
    }

    private static double cariJmlIndex(int n, int y){
        return Math.ceil(((double)n/(double)y))+1;
    }

    //untuk cari di nomor 3
    private static boolean TabelAda(String tabs, List<Tabel> tbl){
        boolean ada = false;
        for(Tabel t : tbl){
            if(t.getNamatabel().equals(tabs)){
                ada = true;
            }
        }
        return ada;
    }
    
    private static Tabel TabelAdaCari(String tabs, List<Tabel> tbl){
        Tabel ada = null;
        for(Tabel t : tbl){
            if(t.getNamatabel().equals(tabs)){
                ada = t;
            }
        }
        return ada;
    }
    

    private static void tulisData(String n){
        n = n + "\n"+"\n";
       try (FileWriter writer = new FileWriter("SharedPool.txt", true);
            BufferedWriter bw = new BufferedWriter(writer)) {
            bw.append(n);
            bw.close();
            //bw.write(n);

       } catch (IOException e) {
           System.err.format("IOException: %s%n", e);
       }
    }
    
    //prosedur buat cek
    private static void tampil(List<Join> j){
        for(Join l : j){
            System.out.print("Tabel : ");
            for(int q =0; q<l.getTabel().size();q++){
                System.out.print(l.getTabel().get(q) +", ");
            }
            System.out.println("");
            System.out.print("Kolom : ");
            for (int k = 0; k < l.getKolom().size(); k++){
                System.out.print(l.getNamakolom(k)+" ");
            }
        }
    }

    private static String readSharedPool(String string) {
       String line = null;
       String baris = "";
       try {
           FileReader fileReader = new FileReader(string);
           BufferedReader bufferedReader = new BufferedReader(fileReader);
           while ((line = bufferedReader.readLine()) != null) {
               baris = baris + line+"\n"; 
           }
           bufferedReader.close();
       } catch (FileNotFoundException ex) {
           System.out.println(
                   "Unable to open file '"
                   + string + "'");
       } catch (IOException ex) {
           System.out.println(
                   "Error reading file '"
                   + string + "'");
       }
       return baris;  
   }

    public static void clearTheFile() throws IOException {
        FileWriter fwOb = new FileWriter("SharedPool.txt", false); 
        PrintWriter pwOb = new PrintWriter(fwOb, false);
        pwOb.flush();
        pwOb.close();
        fwOb.close();
    }
     
     
}
