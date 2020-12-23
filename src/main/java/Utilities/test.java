package Utilities;

public class test {



    public static void main(String[] args){


        String test = "Expected attribute value 'H7734D03D18044C1C85893F1AFD9AC45B' but was 'H07B8596AA2B747ADB5EFCCCCAE613BB7' - comparing <subsection id=\"H7734D03D18044C1C85893F1AFD9AC45B\"...> at /bill[1]/legis-body[1]/section[2]/subsection[1]/quoted-block[1]/section[1]/subsection[4]/@id to <subsection id=\"H07B8596AA2B747ADB5EFCCCCAE613BB7\"...> at /bill[1]/legis-body[1]/section[2]/subsection[1]/quoted-block[1]/section[1]/subsection[4]/@id\n";
        String[] res = test.split("Expected|but was|- comparing");


        System.out.println(res.length);




    }
}
