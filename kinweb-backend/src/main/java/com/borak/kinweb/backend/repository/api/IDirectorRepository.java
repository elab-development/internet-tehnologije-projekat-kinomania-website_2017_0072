/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.borak.kinweb.backend.repository.api;

import com.borak.kinweb.backend.exceptions.DatabaseException;
import java.util.List;

/**
 *
 * @author Mr. Poyo
 */
public interface IDirectorRepository<D, ID> extends IRepository<D, ID> {

    public List<D> findAllByMediaId(ID id) throws DatabaseException,IllegalArgumentException;

}
