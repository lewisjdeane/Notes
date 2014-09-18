package uk.me.lewisdeane.materialnotes.objects;

import java.util.ArrayList;

/**
 * Created by Lewis on 18/09/2014.
 */
public class Pipe<E> extends ArrayList<E> {

    public E pop(){
        E finalTerm = this.peek();
        this.remove(0);
        return finalTerm;
    }

    public E peek() throws IndexOutOfBoundsException {

        E finalTerm;

        try {
            finalTerm = this.get(0);
            this.remove(0);
        } catch(IndexOutOfBoundsException e){
            throw e;
        }

        return finalTerm;

    }

}
