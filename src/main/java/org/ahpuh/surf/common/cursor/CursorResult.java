package org.ahpuh.surf.common.cursor;

import java.util.List;

public record CursorResult<T>(List<T> values, Boolean hasNext) {
}
