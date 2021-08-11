//
// Author: Bruno Lima
// Company: M4I
// 10/08/2021 at 14:36
//

package com.m4i.manutencao.whatsappclone.helper;

import android.util.Base64;

public class Base64Custom {

    public static String encodeBase64(String text) {
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)","");
    }

    public static String decodeBase64(String encodedText){
        return new String(Base64.decode(encodedText, Base64.DEFAULT));
    }

}
