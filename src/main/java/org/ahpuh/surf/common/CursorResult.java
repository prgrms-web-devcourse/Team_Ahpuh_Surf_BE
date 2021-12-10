package org.ahpuh.surf.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CursorResult<T> {

    private List<T> values;

    private Boolean hasNext;

}
