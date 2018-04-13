package org.livingdoc.repositories

import org.livingdoc.repositories.model.decisiontable.DecisionTable
import org.livingdoc.repositories.model.scenario.Scenario

open class Document(val tables: List<DecisionTable>, val lists: List<Scenario>)
