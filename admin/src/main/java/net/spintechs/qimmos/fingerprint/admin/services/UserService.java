package net.spintechs.qimmos.fingerprint.admin.services;

import android.content.Context;


import net.spintechs.qimmos.fingerprint.admin.dao.UserDao;
import net.spintechs.qimmos.fingerprint.admin.model.User;
import net.spintechs.qimmos.fingerprint.admin.tools.DateUtility;

import java.util.Date;
import java.util.List;

public class UserService {

    private UserDao getDao() {
        return new UserDao();
    }

    public boolean insertUser(User user, Context context) {
        UserDao userDao = getDao();

        if (!userDao.insertUser(user, context)) {
            return false;
        }


        return true;
    }

    public String getLastCreatedUser(Context context) {

        Date date, date2;
        UserDao userDao = getDao();
        List<User> users = userDao.getUsersList(context);

        if (users.size() > 0) {
            date = DateUtility.stringToDateTime(users.get(0).getCreatedOn());
            for (int i = 1; i < users.size(); i++) {
                date2 = DateUtility.stringToDateTime(users.get(i).getCreatedOn());
                if (date2.after(date)) {
                    date = date2;
                }
            }
        } else {
            return null;
        }

        return DateUtility.dateToString(date);
    }

    public List<User> getUsers(Context context){

        UserDao userDao = getDao();
        List<User> users = userDao.getUsersList(context);

        return users;

    }

}
