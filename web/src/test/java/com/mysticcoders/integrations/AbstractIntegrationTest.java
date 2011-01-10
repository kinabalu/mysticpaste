package com.mysticcoders.integrations;

import org.unitils.UnitilsJUnit4;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.springframework.context.ApplicationContext;

/**
 * Copyright 2009 Mystic Coders, LLC
 *
 * @author: joed
 * Date  : Mar 7, 2009
 */
@SpringApplicationContext({"/applicationContext.xml", "applicationContext-test.xml"})
public abstract class AbstractIntegrationTest extends UnitilsJUnit4 {


    private ApplicationContext applicationContext;

}
