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

package controllers.register.beneficiaries

import base.SpecBase
import controllers.register.beneficiaries.classofbeneficiaries.{routes => classOfBeneficiariesRoutes}
import controllers.register.beneficiaries.individualBeneficiary.{routes => individualRoutes}
import forms.{AddABeneficiaryFormProvider, YesNoFormProvider}
import models.Status.Completed
import models.core.pages.{Description, FullName}
import models.registration.pages.AddABeneficiary
import models.{NormalMode, UserAnswers}
import pages.entitystatus.{ClassBeneficiaryStatus, IndividualBeneficiaryStatus}
import pages.register.beneficiaries.AddABeneficiaryPage
import pages.register.beneficiaries.charityortrust.charity.CharityNamePage
import pages.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionPage
import pages.register.beneficiaries.large.LargeBeneficiaryDescriptionPage
import pages.register.beneficiaries.other.DescriptionPage
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.AddABeneficiaryViewHelper
import viewmodels.AddRow
import views.html.register.beneficiaries.{AddABeneficiaryView, AddABeneficiaryYesNoView, MaxedOutBeneficiariesView}

class AddABeneficiaryControllerSpec extends SpecBase {

  private def onwardRoute: Call = Call("GET", "/foo")

  private def removeIndividualRoute(index : Int): String =
    individualRoutes.RemoveIndividualBeneficiaryController.onPageLoad(index, fakeDraftId).url

  private def removeClassRoute(index : Int): String =
    classOfBeneficiariesRoutes.RemoveClassOfBeneficiaryController.onPageLoad(index, fakeDraftId).url

  private def changeIndividualRoute(index: Int): String =
    individualRoutes.AnswersController.onPageLoad(index, fakeDraftId).url

  private def changeClassOfBeneficiariesRoute(index: Int): String =
    classOfBeneficiariesRoutes.ClassBeneficiaryDescriptionController.onPageLoad(index, fakeDraftId).url

  private lazy val addABeneficiaryRoute = routes.AddABeneficiaryController.onPageLoad(fakeDraftId).url

  private lazy val addOnePostRoute = routes.AddABeneficiaryController.submitOne(fakeDraftId).url

  private lazy val addAnotherPostRoute = routes.AddABeneficiaryController.submitAnother(fakeDraftId).url

  private lazy val submitCompleteRoute = routes.AddABeneficiaryController.submitComplete(fakeDraftId).url

  private val formProvider = new AddABeneficiaryFormProvider()
  private val form = formProvider()

  private val yesNoForm = new YesNoFormProvider().withPrefix("addABeneficiaryYesNo")

  private lazy val beneficiariesComplete = List(
    AddRow("First Last", typeLabel = "Individual Beneficiary", changeIndividualRoute(0), removeIndividualRoute(0)),
    AddRow("description", typeLabel = "Class of beneficiaries", changeClassOfBeneficiariesRoute(0), removeClassRoute(0))
  )

  private val userAnswersWithBeneficiariesComplete = emptyUserAnswers
    .set(pages.register.beneficiaries.individual.NamePage(0), FullName("First", None, "Last")).success.value
    .set(IndividualBeneficiaryStatus(0), Completed).success.value
    .set(ClassBeneficiaryDescriptionPage(0), "description").success.value
    .set(ClassBeneficiaryStatus(0), Completed).success.value

  private def genTrustBeneficiaries(userAnswers: UserAnswers, range: Int) = {
    (0 until range)
      .foldLeft(emptyUserAnswers)((ua,index) => ua.set(pages.register.beneficiaries.charityortrust.trust.NamePage(index), "Company Name").success.value)
  }

  private def genCompanyBeneficiaries(userAnswers: UserAnswers, range: Int) = {
    (0 until range)
      .foldLeft(emptyUserAnswers)((ua,index) => ua.set(pages.register.beneficiaries.companyoremploymentrelated.company.NamePage(index), "Trust Name").success.value)
  }

  private def genIndividualBeneficiaries(userAnswers: UserAnswers, range: Int) = {
    (0 until range)
      .foldLeft(emptyUserAnswers)((ua,index) => ua.set(
          pages.register.beneficiaries.individual.NamePage(index),
          FullName("first name", None, "last name")
        ).success.value
      )
  }

  private def genUnidentifiedBeneficiaries(userAnswers: UserAnswers, range: Int) = {
    (0 until range)
      .foldLeft(emptyUserAnswers)((ua,index) => ua.set(ClassBeneficiaryDescriptionPage(index), s"description $index").success.value)
  }

  private def genCharityBeneficiaries(userAnswers: UserAnswers, range: Int) = {
    (0 until range)
      .foldLeft(emptyUserAnswers)((ua,index) => ua.set(CharityNamePage(index), s"Charity name $index").success.value)
  }

  private def genLargeBeneficiaries(userAnswers: UserAnswers, range: Int) = {
    (0 until range)
      .foldLeft(emptyUserAnswers)((ua,index) => ua.set(LargeBeneficiaryDescriptionPage(index), Description(s"description $index", None, None, None, None)).success.value)
  }

  private def genOtherBeneficiaries(userAnswers: UserAnswers, range: Int) = {
    (0 until range)
      .foldLeft(emptyUserAnswers)((ua,index) => ua.set(DescriptionPage(index), s"Other description $index").success.value)
  }

  "AddABeneficiary Controller" when {

    "no data" must {

      "redirect to Session Expired for a GET if no existing data is found" in {
        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, addAnotherPostRoute)
            .withFormUrlEncodedBody(("value", AddABeneficiary.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "there are no beneficiaries" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddABeneficiaryYesNoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, NormalMode, fakeDraftId)(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, addOnePostRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, addOnePostRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = yesNoForm.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[AddABeneficiaryYesNoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, NormalMode, fakeDraftId)(fakeRequest, messages).toString

        application.stop()
      }

    }

    "there are beneficiaries" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithBeneficiariesComplete)).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddABeneficiaryView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, NormalMode, fakeDraftId, Nil, beneficiariesComplete, "You have added 2 beneficiaries", Nil)(fakeRequest, messages).toString

        application.stop()
      }

      "populate the view without value on a GET when the question has previously been answered" in {
        val userAnswers = userAnswersWithBeneficiariesComplete.
          set(AddABeneficiaryPage,AddABeneficiary.YesNow).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddABeneficiaryView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, NormalMode, fakeDraftId, Nil, beneficiariesComplete, "You have added 2 beneficiaries", Nil)(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(userAnswersWithBeneficiariesComplete)).build()

        val request =
          FakeRequest(POST, addAnotherPostRoute)
            .withFormUrlEncodedBody(("value", AddABeneficiary.options.head.value))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, addAnotherPostRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddABeneficiaryView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, NormalMode, fakeDraftId, Nil, Nil, "Add a beneficiary", Nil)(fakeRequest, messages).toString

        application.stop()
      }

    }

    "maxed out beneficiaries" must {

      "return correct view when all are maxed out" in {

        val beneficiaries = List(
          genTrustBeneficiaries(emptyUserAnswers, 25),
          genIndividualBeneficiaries(emptyUserAnswers, 25),
          genUnidentifiedBeneficiaries(emptyUserAnswers, 25),
          genCompanyBeneficiaries(emptyUserAnswers, 25),
          genCharityBeneficiaries(emptyUserAnswers, 25),
          genLargeBeneficiaries(emptyUserAnswers, 25),
          genOtherBeneficiaries(emptyUserAnswers, 25)
        )

        val userAnswers = beneficiaries.foldLeft(emptyUserAnswers)((x, acc) => acc.copy(data = x.data.deepMerge(acc.data)))

        val beneficiaryRows = new AddABeneficiaryViewHelper(userAnswers, fakeDraftId).rows

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[MaxedOutBeneficiariesView]

        status(result) mustEqual OK

        val content = contentAsString(result)

        content mustEqual view(fakeDraftId, beneficiaryRows.inProgress, beneficiaryRows.complete, "You have added 175 beneficiaries")(fakeRequest, messages).toString
        content must include("You cannot enter another beneficiary as you have entered a maximum of 175.")
        content must include("If you have further beneficiaries to add, write to HMRC with their details.")

        application.stop()

      }

      "return correct view when one type of beneficiary is maxed out" in {

        val beneficiaries = List(
          genTrustBeneficiaries(emptyUserAnswers, 0),
          genIndividualBeneficiaries(emptyUserAnswers, 0),
          genUnidentifiedBeneficiaries(emptyUserAnswers, 0),
          genCompanyBeneficiaries(emptyUserAnswers, 0),
          genCharityBeneficiaries(emptyUserAnswers, 25),
          genLargeBeneficiaries(emptyUserAnswers, 0),
          genOtherBeneficiaries(emptyUserAnswers, 0)
        )

        val userAnswers = beneficiaries.foldLeft(emptyUserAnswers)((x, acc) => acc.copy(data = x.data.deepMerge(acc.data)))

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        contentAsString(result) must include("You cannot add another charity as you have entered a maximum of 25.")
        contentAsString(result) must include("If you have further beneficiaries to add within this type, write to HMRC with their details.")

        application.stop()
      }

      "return correct view when more than one type of beneficiary is maxed out" in {

        val beneficiaries = List(
          genTrustBeneficiaries(emptyUserAnswers, 0),
          genIndividualBeneficiaries(emptyUserAnswers, 25),
          genUnidentifiedBeneficiaries(emptyUserAnswers, 0),
          genCompanyBeneficiaries(emptyUserAnswers, 0),
          genCharityBeneficiaries(emptyUserAnswers, 25),
          genLargeBeneficiaries(emptyUserAnswers, 0),
          genOtherBeneficiaries(emptyUserAnswers, 0)
        )

        val userAnswers = beneficiaries.foldLeft(emptyUserAnswers)((x, acc) => acc.copy(data = x.data.deepMerge(acc.data)))

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        contentAsString(result) must include("You have entered the maximum number of beneficiaries for:")
        contentAsString(result) must include("If you have further beneficiaries to add within these types, write to HMRC with their details.")

        application.stop()

      }

      "redirect to registration progress when user clicks continue" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(POST, submitCompleteRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual "http://localhost:9781/trusts-registration/draftId/registration-progress"

        application.stop()

      }

    }

  }
}
