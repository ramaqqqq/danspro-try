package com.micro.demo.helpers;

public class FormatErr {

    public static Exception loginError(String err) {
        if (err.contains("email")) {
            Helpers.log("error", "In Server: Email yang anda masukan salah");
            return new Exception("Email yang anda masukan salah");
        }

        if (err.contains("hashedPassword")) {
            Helpers.log("error", "In Server: Password yang anda masukan salah");
            return new Exception("Password yang anda masukan salah");
        }

        return new Exception(err);
    }

    public static Exception formatError(String err) {
        if (err.contains("email")) {
            Helpers.log("error", "In Server: Email yang anda masukan salah");
            return new Exception("Email sudah di gunakan");
        }

        if (err.contains("hashedPassword")) {
            Helpers.log("error", "In Server: Password yang anda masukan salah");
            return new Exception("Password yang anda masukan salah");
        }

        return new Exception(err);
    }

}
