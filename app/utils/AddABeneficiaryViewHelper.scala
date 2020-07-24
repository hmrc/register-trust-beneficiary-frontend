/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package utils

import controllers.register.beneficiaries.routes
import models.UserAnswers
import models.core.pages.FullName
import play.api.i18n.Messages
import sections.beneficiaries.{CharityBeneficiaries, ClassOfBeneficiaries, IndividualBeneficiaries}
import viewmodels.addAnother.{CharityBeneficiaryViewModel, ClassOfBeneficiaryViewModel, IndividualBeneficiaryViewModel}
import viewmodels.{AddRow, AddToRows}

class AddABeneficiaryViewHelper(userAnswers: UserAnswers, draftId : String)(implicit messages: Messages) {

  private case class InProgressComplete(inProgress : List[AddRow], complete: List[AddRow])

  private def parseName(name : Option[String]) : String = {
    val defaultValue = messages("entities.no.name.added")
    name.getOrElse(defaultValue)
  }

  private def parseIndividualBeneficiary(individualBeneficiary : (IndividualBeneficiaryViewModel, Int)) : AddRow = {
    val vm = individualBeneficiary._1
    val index = individualBeneficiary._2

    AddRow(
      name = parseName(vm.name.map(_.toString)),
      typeLabel = messages("entities.beneficiary.individual"),
      changeUrl = controllers.routes.FeatureNotAvailableController.onPageLoad().url,
      removeUrl = routes.RemoveIndividualBeneficiaryController.onPageLoad(index, draftId).url
    )
  }

  private def parseClassOfBeneficiary(classOfBeneficiary : (ClassOfBeneficiaryViewModel, Int)) : AddRow = {

    val vm = classOfBeneficiary._1
    val index = classOfBeneficiary._2

    val defaultValue = messages("entities.no.description.added")
    AddRow(
      vm.description.getOrElse(defaultValue),
      messages("entities.beneficiary.class"),
      controllers.routes.FeatureNotAvailableController.onPageLoad().url,
      removeUrl = routes.RemoveClassOfBeneficiaryController.onPageLoad(index, draftId).url
    )
  }

  private def parseCharityBeneficiary(charityBeneficiary:(CharityBeneficiaryViewModel, Int)): AddRow = {

    val vm = charityBeneficiary._1
    val index = charityBeneficiary._2

    AddRow(
      name = parseName(vm.name),
      messages("entities.beneficiary.charity"),
      changeUrl = controllers.register.beneficiaries.charityOrTrust.charity.routes.CharityAnswersController.onPageLoad(index, draftId).url,
      removeUrl = controllers.routes.FeatureNotAvailableController.onPageLoad().url
    )
  }

  private def individualBeneficiaries = {
    val individualBeneficiaries = userAnswers.get(IndividualBeneficiaries).toList.flatten.zipWithIndex

    val indBeneficiaryComplete = individualBeneficiaries.filter(_._1.isComplete).map(parseIndividualBeneficiary)

    val indBenInProgress = individualBeneficiaries.filterNot(_._1.isComplete).map(parseIndividualBeneficiary)

    InProgressComplete(inProgress = indBenInProgress, complete = indBeneficiaryComplete)
  }

  private def classOfBeneficiaries = {
    val classOfBeneficiaries = userAnswers.get(ClassOfBeneficiaries).toList.flatten.zipWithIndex

    val completed = classOfBeneficiaries.filter(_._1.isComplete).map(parseClassOfBeneficiary)

    val progress = classOfBeneficiaries.filterNot(_._1.isComplete).map(parseClassOfBeneficiary)

    InProgressComplete(inProgress = progress, complete = completed)
  }

  private def charityBeneficiaries = {
    val charityBeneficiaries = userAnswers.get(CharityBeneficiaries).toList.flatten.zipWithIndex

    val completed = charityBeneficiaries.filter(_._1.isComplete).map(parseCharityBeneficiary)

    val progress = charityBeneficiaries.filterNot(_._1.isComplete).map(parseCharityBeneficiary)

    InProgressComplete(inProgress = progress, complete = completed)
  }

  def rows : AddToRows =
    AddToRows(
      inProgress = individualBeneficiaries.inProgress ::: classOfBeneficiaries.inProgress ::: charityBeneficiaries.inProgress,
      complete = individualBeneficiaries.complete ::: classOfBeneficiaries.complete ::: charityBeneficiaries.complete
    )

}
