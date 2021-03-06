package com.example.memoscopio;

import org.json.JSONObject;

public class User {

    private String name;
    private String lastname;
    private String dni;
    private String password;
    private String commission;

    public static String token = "";
    public static String token_refresh = "";
    public static String email = "";

    public User(String name, String lastname, String dni, String email, String password, String commission){
        this.name = name;
        this.lastname = lastname;
        this.dni = dni;
        User.email = email;
        this.password = password;
        this.commission = commission;
    }

    public User(String email, String password){
        User.email = email;
        this.password = password;
    }

    // devuelve un JSON-String basado en los datos de inicializacion, para el registro
    protected String registerData(){
        JSONObject user = new JSONObject();
        try {
            user.put("name", name);
            user.put("lastname", lastname);
            user.put("dni", dni);
            user.put("email", User.email);
            user.put("password", password);
            user.put("commission", commission);
        } catch (Exception e){
            e.printStackTrace();
        }

        return user.toString();
    }

    // devuelve un JSON-String basado en los datos de inicializacion, para el login
    protected String loginData(){
        JSONObject user = new JSONObject();
        try {
            user.put("email", User.email);
            user.put("password", password);
        } catch (Exception e){
            e.printStackTrace();
        }

        return user.toString();
    }

    // valida los datos para el registro
    protected String validateRegister(){
        if (name.length() == 0) {
            return("El nombre no puede estar en blanco");
        }

        if (lastname.length() == 0) {
            return("El apellido no puede estar en blanco");
        }

        if (dni.length() == 0) {
            return("El DNI no puede estar en blanco");
        }

        if (User.email.length() == 0) {
            return("El email no puede estar en blanco");
        }

        if (!User.email.matches(Constants.EMAIL_PATTERN)) {
            return("Formato de email invalido");
        }

        if (password.length() < 8) {
            return("La longitud del password debe ser de 8 caracteres como minimo");
        }

        if (commission.length() == 0) {
            return("La comision no puede estar en blanco");
        }

        return "ok";
    }

    // valida los datos para el login
    protected String validateLogin(){
        if (User.email.length() == 0) {
            return("El email no puede estar en blanco");
        }

        if (!User.email.matches(Constants.EMAIL_PATTERN)) {
            return("Formato de email invalido");
        }

        if (password.length() < 8) {
            return("La longitud del password debe ser de 8 caracteres como minimo");
        }

        return "ok";
    }


}
