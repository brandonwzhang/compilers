package com.bwz6jk2227esl89ahj34.optimization.available_copies;

import com.bwz6jk2227esl89ahj34.ir.IRTemp;
import com.bwz6jk2227esl89ahj34.optimization.LatticeElement;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * Created by jihunkim on 4/20/16.
 */
@Data
@AllArgsConstructor
public class AvailableCopiesSet extends LatticeElement {
    private Map<IRTemp, IRTemp> map;
}
