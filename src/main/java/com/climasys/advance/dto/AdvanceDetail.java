package com.climasys.advance.dto;

import java.math.BigDecimal;

/**
 * Interface projection for advance collection details
 * Used by AdvanceCollectionRepository to map native query results
 */
public interface AdvanceDetail {
    String getAdvanceDate();
    BigDecimal getAdvance();
}

