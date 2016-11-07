package br.com.compras.app.service;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import br.com.mariel.compras.domain.Phone;
import br.com.mariel.compras.domain.User;

/**
 * Created by elvis.souza on 20/07/2016.
 */
public class ContactUtil {

    private ContentResolver mContentResolver;

    public ContactUtil(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
    }

    // Chama a Intent nativa da agenda
    public Intent getContatoFromIntent() {
        return (new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI));
    }

    // Pega os contatos via Query
    public List<User> getContatosFromQuery() {
        // Para melhorar o desempenho, informamos apenas as colunas que irão ser lidas.
        final String[] projection = {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };

        // Consulta os contatos usando o ContentResolver
        Cursor c = mContentResolver.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);

        // Lê o cursor e retorna uma lista de Contatos
        return getContatosFromCursor(c);
    }

    // Este método recebe um cursor e retorna uma lista de contatos
    public List<User> getContatosFromCursor(Cursor c) {
        List<User> users = new ArrayList<User>();

        // Percorre todo o cursor
        if (c.getCount() > 0) {
            while (c.moveToNext()) {
                User user = User.reload("");

                String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

                // Nome
                user.setUser_name(c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

                // Telefones
                user.setPhones(getTelefones(id));

                users.add(user);
            }
        }

        return users;
    }

    // Este método retona a lista de Telefones do contato
    public List<Phone> getTelefones(String id) {
        List<Phone> phones = new ArrayList<Phone>();

        // Colunas da consulta (otimização)
        final String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER };

        // Consulta os telefones filtrando pelo Id do contato
        Cursor c = mContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);

        while (c.moveToNext())
            phones.add(Phone.reload(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))));

        c.close();
        return (phones);
    }
}
