package org.ahpuh.surf.common.response;

import java.util.List;

public record CursorResult<T>(List<T> values, Boolean hasNext) {
}
