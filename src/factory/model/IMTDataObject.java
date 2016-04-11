/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package factory.model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Topo River
 */
public class IMTDataObject {

    private List<IMTAward> award_list = new LinkedList<IMTAward>();
    private IMTError error;

    public IMTDataObject() {
    }

    public IMTDataObject(List<IMTAward> award_list) {

        this.award_list = award_list;
    }

    /**
     * @return the error
     */
    public IMTError getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(IMTError error) {
        this.error = error;
    }
}
