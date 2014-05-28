/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.component.user;

/**
 *
 * @author KH
 */
public class UserAccountLockedException extends Exception{

    public UserAccountLockedException(String username) {
        super("I'm sorry but user account "+username+" is locked. Please contact administrator to unlock it.");
    }
}
