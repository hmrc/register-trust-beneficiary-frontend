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

package views.register.beneficiaries.charityortrust.charity

import forms.IncomePercentageFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.IntViewBehaviours
import views.html.register.beneficiaries.charityortrust.charity.HowMuchIncomeView

class HowMuchIncomeViewSpec extends IntViewBehaviours {

  val prefix = "charity.shareOfIncome"

  val form: Form[Int] = new IncomePercentageFormProvider().withPrefix(prefix)
  val charityName = "Test"
  val index = 0

  "HowMuchIncome view" must {

    val view = viewFor[HowMuchIncomeView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, index, charityName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), prefix, charityName)

    behave like pageWithBackLink(applyView(form))

    behave like intPage(form, applyView, prefix, charityName)

    behave like pageWithASubmitButton(applyView(form))

  }
}
