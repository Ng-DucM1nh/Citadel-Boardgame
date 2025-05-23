package citadels;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.*;

public class testJson
{
  public static void main(String[] args)
  {
    // JSONObject obj = new JSONObject();
    // // String jsonText;

    // // obj.put("name", "foo");
    // // obj.put("num", new Integer(100));
    // // obj.put("balance", new Double(1000.21));
    // // obj.put("is_vip", new Boolean(true));
    // // jsonText = obj.toString();
    // // System.out.println(jsonText);

    // String s = "{\"balance\":1000.21,\"is_vip\":true,\"num\":100,\"name\":\"foo\"}";
    // JSONObject omn = (JSONObject) JSONValue.parse(s);
    // System.out.println(omn.get("balance"));

    // String jc = c.toJSON().toString();
    // JSONObject obj = (JSONObject) JSONValue.parse(jc);
    // JSONArray list = new JSONArray();
    // JSONObject t1 = new JSONObject();
    // t1.put("nig","ga");
    // list.add(obj); list.add(t1);
    // System.out.println(list.toString());
    // JSONObject o2 = new JSONObject();
    // o2.put("eo",list);
    // System.out.println(o2.toString());

    Card c = new Card("a", "red", 1, 7, "yeye");
    JSONObject obj = c.toJSON();
    System.out.println(obj.toString());
  }
}