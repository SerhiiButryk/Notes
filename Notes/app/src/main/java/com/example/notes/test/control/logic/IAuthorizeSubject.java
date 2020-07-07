package com.example.notes.test.control.logic;

public interface IAuthorizeSubject {

    void subscribe(IAuthorize observer);

    void unsubscribe(IAuthorize observer);

    void notifyObservers(AuthorizeType authorizeType);
}
