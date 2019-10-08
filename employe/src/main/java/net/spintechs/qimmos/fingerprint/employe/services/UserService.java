package net.spintechs.qimmos.fingerprint.employe.services;

import android.content.Context;

import net.spintechs.qimmos.fingerprint.employe.dao.UserDao;
import net.spintechs.qimmos.fingerprint.employe.model.User;

public class UserService {

    private UserDao getDao(){
        return new UserDao();
    }

    public boolean isEmpty(Context context){
        return getDao().isEmpty(context);
    }

    public boolean insertUser(User user, Context context) {

        UserDao userDao = getDao();
        if(userDao.insertUser(user, context)){
            return true;
        }

        return false;

    }

    public User getUser(String id, Context context){

        UserDao userDao = getDao();
        return userDao.getUser(id, context);

    }

    public boolean resetAll(Context context){
        return getDao().reset(context);
    }

    public long countUsers(Context context){
        return getDao().countUsers(context);
    }

}
