/*
 * Copyright 2023 HM Revenue & Customs
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

package views.register.beneficiaries

import play.twirl.api.HtmlFormat
import viewmodels.AddRow
import views.behaviours.{OptionsViewBehaviours, TabularDataViewBehaviours}
import views.html.register.beneficiaries.MaxedOutBeneficiariesView

class MaxedOutBeneficiariesViewSpec extends OptionsViewBehaviours with TabularDataViewBehaviours {

  val messageKeyPrefix = "addABeneficiary"

  val view: MaxedOutBeneficiariesView = viewFor[MaxedOutBeneficiariesView](Some(emptyUserAnswers))

  val rows: List[AddRow] = List(AddRow("Charity", "", "", ""), AddRow("Trust", "", "", ""))

  def applyView(): HtmlFormat.Appendable =
    view.apply("draftId", rows, rows, "Add a beneficiary")(fakeRequest, messages)

  "MaxedOutBeneficiaryView" when {

    "there are many maxed out beneficiaries" must {
      val view = applyView()

      behave like normalPage(view, messageKeyPrefix)

      behave like pageWithBackLink(view)

      behave like pageWithTabularData(view, rows, rows)

      behave like pageWithASubmitButton(view)

      "content shows maxed beneficiaries" in {
        val doc = asDocument(view)

        assertContainsText(doc, "You cannot enter another beneficiary as you have entered a maximum of 175.")
        assertContainsText(doc, "Check the beneficiaries you have added. If you have further beneficiaries to add, write to HMRC with their details.")
      }
    }
  }

}
