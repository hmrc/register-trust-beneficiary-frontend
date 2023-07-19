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

package controllers.register.beneficiaries

import base.SpecBase
import cats.data.EitherT
import controllers.register.beneficiaries.charityortrust.charity.{routes => charityRoutes}
import controllers.register.beneficiaries.charityortrust.trust.{routes => trustRoutes}
import controllers.register.beneficiaries.classofbeneficiaries.{routes => classOfBeneficiariesRoutes}
import controllers.register.beneficiaries.companyoremploymentrelated.company.{routes => companyRoutes}
import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.{routes => largeRoutes}
import controllers.register.beneficiaries.individualBeneficiary.{routes => individualRoutes}
import controllers.register.beneficiaries.other.{routes => otherRoutes}
import errors.{ServerError, TrustErrors}
import forms.{AddABeneficiaryFormProvider, YesNoFormProvider}
import models.CompanyOrEmploymentRelatedToAdd.Company
import models.Status.{Completed, InProgress}
import models.core.pages.{Description, FullName}
import models.registration.pages.AddABeneficiary
import models.registration.pages.CharityOrTrust.Charity
import models.registration.pages.KindOfTrust._
import models.registration.pages.RoleInCompany.Employee
import models.registration.pages.WhatTypeOfBeneficiary.Individual
import models.{ReadOnlyUserAnswers, TaskStatus, UserAnswers}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{any, eq => mEq}
import org.scalatest.BeforeAndAfterEach
import pages.entitystatus._
import pages.register.KindOfTrustPage
import pages.register.beneficiaries.charityortrust.{CharityOrTrustPage, charity => charityPages, trust => trustPages}
import pages.register.beneficiaries.companyoremploymentrelated.employmentRelated.{LargeBeneficiaryDescriptionPage, LargeBeneficiaryNamePage}
import pages.register.beneficiaries.companyoremploymentrelated.{CompanyOrEmploymentRelatedPage, company => companyPages}
import pages.register.beneficiaries.{AddABeneficiaryPage, WhatTypeOfBeneficiaryPage, classofbeneficiaries => classOfBeneficiariesPages, individual => individualPages, other => otherPages}
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustsStoreService
import utils.{AddABeneficiaryViewHelper, RegistrationProgress}
import viewmodels.AddRow
import views.html.TechnicalErrorView
import views.html.register.beneficiaries.{AddABeneficiaryView, AddABeneficiaryYesNoView, MaxedOutBeneficiariesView}

import scala.concurrent.Future

class AddABeneficiaryControllerSpec extends SpecBase with BeforeAndAfterEach {

  private def onwardRoute: Call = Call("GET", "/foo")

  private val index: Int = 0
  private val max: Int = 25

  private lazy val removeIndividualRoute: String =
    individualRoutes.RemoveIndividualBeneficiaryController.onPageLoad(index, fakeDraftId).url

  private lazy val removeClassRoute: String =
    classOfBeneficiariesRoutes.RemoveClassOfBeneficiaryController.onPageLoad(index, fakeDraftId).url

  private lazy val removeCharityRoute: String =
    charityRoutes.RemoveCharityBeneficiaryController.onPageLoad(index, fakeDraftId).url

  private lazy val removeTrustRoute: String =
    trustRoutes.RemoveTrustBeneficiaryController.onPageLoad(index, fakeDraftId).url

  private lazy val removeCompanyRoute: String =
    companyRoutes.RemoveCompanyBeneficiaryController.onPageLoad(index, fakeDraftId).url

  private lazy val removeLargeRoute: String =
    largeRoutes.RemoveEmploymentRelatedBeneficiaryController.onPageLoad(index, fakeDraftId).url

  private lazy val removeOtherRoute: String =
    otherRoutes.RemoveOtherBeneficiaryController.onPageLoad(index, fakeDraftId).url

  private lazy val changeIndividualRoute: String =
    individualRoutes.AnswersController.onPageLoad(index, fakeDraftId).url

  private lazy val changeClassOfBeneficiariesRoute: String =
    classOfBeneficiariesRoutes.ClassBeneficiaryDescriptionController.onPageLoad(index, fakeDraftId).url

  private lazy val changeCharityRoute: String =
    charityRoutes.CharityAnswersController.onPageLoad(index, fakeDraftId).url

  private lazy val changeTrustRoute: String =
    trustRoutes.AnswersController.onPageLoad(index, fakeDraftId).url

  private lazy val changeCompanyRoute: String =
    companyRoutes.CheckDetailsController.onPageLoad(index, fakeDraftId).url

  private lazy val changeLargeRoute: String =
    largeRoutes.CheckDetailsController.onPageLoad(index, fakeDraftId).url

  private lazy val changeOtherRoute: String =
    otherRoutes.CheckDetailsController.onPageLoad(index, fakeDraftId).url

  private lazy val addABeneficiaryRoute = routes.AddABeneficiaryController.onPageLoad(fakeDraftId).url

  private lazy val addOnePostRoute = routes.AddABeneficiaryController.submitOne(fakeDraftId).url

  private lazy val addAnotherPostRoute = routes.AddABeneficiaryController.submitAnother(fakeDraftId).url

  private lazy val submitCompleteRoute = routes.AddABeneficiaryController.submitComplete(fakeDraftId).url

  private val formProvider = new AddABeneficiaryFormProvider()
  private val form = formProvider()

  private val yesNoForm = new YesNoFormProvider().withPrefix("addABeneficiaryYesNo")

  private lazy val beneficiariesComplete = List(
    AddRow("Individual Name", typeLabel = "Named individual", changeIndividualRoute, removeIndividualRoute),
    AddRow("Unidentified Description", typeLabel = "Class of beneficiaries", changeClassOfBeneficiariesRoute, removeClassRoute),
    AddRow("Charity Name", typeLabel = "Named charity", changeCharityRoute, removeCharityRoute),
    AddRow("Trust Name", typeLabel = "Named trust", changeTrustRoute, removeTrustRoute),
    AddRow("Company Name", typeLabel = "Named company", changeCompanyRoute, removeCompanyRoute),
    AddRow("Large Name", typeLabel = "Employment related", changeLargeRoute, removeLargeRoute),
    AddRow("Other Description", typeLabel = "Other beneficiary", changeOtherRoute, removeOtherRoute)
  )

  private val userAnswersWithBeneficiariesComplete = emptyUserAnswers
    .set(individualPages.NamePage(index), FullName("Individual", None, "Name")).value
    .set(IndividualBeneficiaryStatus(index), Completed).value

    .set(classOfBeneficiariesPages.ClassBeneficiaryDescriptionPage(index), "Unidentified Description").value
    .set(ClassBeneficiaryStatus(index), Completed).value

    .set(charityPages.CharityNamePage(index), "Charity Name").value
    .set(CharityBeneficiaryStatus(index), Completed).value

    .set(trustPages.NamePage(index), "Trust Name").value
    .set(TrustBeneficiaryStatus(index), Completed).value

    .set(companyPages.NamePage(index), "Company Name").value
    .set(CompanyBeneficiaryStatus(index), Completed).value

    .set(LargeBeneficiaryNamePage(index), "Large Name").value
    .set(LargeBeneficiaryStatus(index), Completed).value

    .set(otherPages.DescriptionPage(index), "Other Description").value
    .set(OtherBeneficiaryStatus(index), Completed).value

  private def genTrustBeneficiaries(range: Int): UserAnswers = {
    (0 until range)
      .foldLeft(emptyUserAnswers)((ua, index) =>
        ua.set(trustPages.NamePage(index), "Company Name").value
      )
  }

  private def genCompanyBeneficiaries(range: Int): UserAnswers = {
    (0 until range)
      .foldLeft(emptyUserAnswers)(
        (ua, index) => ua.set(companyPages.NamePage(index), "Trust Name").value
      )
  }

  private def genIndividualBeneficiaries(range: Int): UserAnswers = {
    (0 until range)
      .foldLeft(emptyUserAnswers)(
        (ua, index) => ua.set(individualPages.NamePage(index), FullName("first name", None, "last name")).value
      )
  }

  private def genUnidentifiedBeneficiaries(range: Int): UserAnswers = {
    (0 until range)
      .foldLeft(emptyUserAnswers)(
        (ua, index) => ua.set(classOfBeneficiariesPages.ClassBeneficiaryDescriptionPage(index), s"Unidentified Description $index").value
      )
  }

  private def genCharityBeneficiaries(range: Int): UserAnswers = {
    (0 until range)
      .foldLeft(emptyUserAnswers)(
        (ua, index) => ua.set(charityPages.CharityNamePage(index), s"Charity Name $index").value
      )
  }

  private def genLargeBeneficiaries(range: Int): UserAnswers = {
    (0 until range)
      .foldLeft(emptyUserAnswers)(
        (ua, index) => ua.set(LargeBeneficiaryDescriptionPage(index), Description(s"Large Name $index", None, None, None, None)).value
      )
  }

  private def genOtherBeneficiaries(range: Int): UserAnswers = {
    (0 until range)
      .foldLeft(emptyUserAnswers)(
        (ua, index) => ua.set(otherPages.DescriptionPage(index), s"Other Description $index").value
      )
  }

  private val mockTrustsStoreService = mock[TrustsStoreService]
  private val mockRegistrationProgress = mock[RegistrationProgress]

  override def beforeEach(): Unit = {
    reset(mockTrustsStoreService, mockRegistrationProgress)

    when(mockTrustsStoreService.updateTaskStatus(any(), any())(any(), any()))
      .thenReturn(EitherT[Future, TrustErrors, Boolean](Future.successful(Right(true))))
  }

  "AddABeneficiary Controller" when {

    "no data" must {

      "redirect to Session Expired for a GET if no existing data is found" in {
        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, addAnotherPostRoute)
            .withFormUrlEncodedBody(("value", AddABeneficiary.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

        application.stop()
      }
    }

    "there are no beneficiaries" must {

      "return OK and the correct view for a GET" in {

        val settlorsAnswers: ReadOnlyUserAnswers = ReadOnlyUserAnswers(
          emptyUserAnswers
            .set(KindOfTrustPage, Intervivos).value
            .set(WhatTypeOfBeneficiaryPage, Individual).value
            .set(CharityOrTrustPage, Charity).value
            .set(CompanyOrEmploymentRelatedPage, Company).value
            .data
        )

        mockRegistrationsRepositoryBuilder(getSettlorsAnswersResult = Right(Some(settlorsAnswers)))

        val application = applicationBuilder(
            userAnswers = Some(emptyUserAnswers),
            mockGetSettlorsAnswersResult = Right(Some(settlorsAnswers))
        ).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddABeneficiaryYesNoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, fakeDraftId)(request, messages).toString

        val uaCaptor: ArgumentCaptor[UserAnswers] = ArgumentCaptor.forClass(classOf[UserAnswers])
        verify(mockRegistrationsRepository).set(uaCaptor.capture)(any(), any())
        uaCaptor.getValue.get(WhatTypeOfBeneficiaryPage) mustNot be(defined)

        application.stop()
      }

      "redirect to the next page when valid data is submitted" when {

        "yes selected" in {
          val application =
            applicationBuilder(userAnswers = Some(emptyUserAnswers))
              .overrides(
                bind[TrustsStoreService].to(mockTrustsStoreService)
              )
              .build()

          val request =
            FakeRequest(POST, addOnePostRoute)
              .withFormUrlEncodedBody(("value", "true"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual onwardRoute.url

          verify(mockTrustsStoreService).updateTaskStatus(mEq(draftId), mEq(TaskStatus.InProgress))(any(), any())

          application.stop()
        }

        "no selected" in {
          val application =
            applicationBuilder(userAnswers = Some(emptyUserAnswers))
              .overrides(
                bind[TrustsStoreService].to(mockTrustsStoreService)
              )
              .build()

          val request =
            FakeRequest(POST, addOnePostRoute)
              .withFormUrlEncodedBody(("value", "false"))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustBe onwardRoute.url

          verify(mockTrustsStoreService).updateTaskStatus(mEq(draftId), mEq(TaskStatus.Completed))(any(), any())

          application.stop()
        }
      }

      "return an Internal Server Error when setting the user answers goes wrong" in {

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers), mockSetResult = Left(ServerError()))
            .overrides(
              bind[TrustsStoreService].to(mockTrustsStoreService)
            )
            .build()

        val request =
          FakeRequest(POST, addOnePostRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual INTERNAL_SERVER_ERROR

        val errorPage = application.injector.instanceOf[TechnicalErrorView]

        contentType(result) mustBe Some("text/html")
        contentAsString(result) mustEqual errorPage()(request, messages).toString

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
          view(boundForm, fakeDraftId)(request, messages).toString

        application.stop()
      }

    }

    "there are beneficiaries" must {

      "return OK and the correct view for a GET" in {

        val settlorsAnswers: ReadOnlyUserAnswers = ReadOnlyUserAnswers(
          emptyUserAnswers.set(KindOfTrustPage, Intervivos).value.data
        )

        mockRegistrationsRepositoryBuilder(getSettlorsAnswersResult = Right(Some(settlorsAnswers)))

        val application = applicationBuilder(userAnswers = Some(userAnswersWithBeneficiariesComplete)).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddABeneficiaryView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, fakeDraftId, Nil, beneficiariesComplete, "You have added 7 beneficiaries", Nil)(request, messages).toString

        application.stop()
      }

      "populate the view without value on a GET when the question has previously been answered" in {

        val settlorsAnswers: ReadOnlyUserAnswers = ReadOnlyUserAnswers(
          emptyUserAnswers.set(KindOfTrustPage, Intervivos).value.data
        )

        mockRegistrationsRepositoryBuilder(getSettlorsAnswersResult = Right(Some(settlorsAnswers)))

        val userAnswers = userAnswersWithBeneficiariesComplete.
          set(AddABeneficiaryPage, AddABeneficiary.YesNow).value

        val application = applicationBuilder(
          userAnswers = Some(userAnswers),
          mockGetSettlorsAnswersResult = Right(Some(settlorsAnswers))
        ).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddABeneficiaryView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, fakeDraftId, Nil, beneficiariesComplete, "You have added 7 beneficiaries", Nil)(request, messages).toString

        application.stop()
      }

      "set individual beneficiary status to in progress" when {

        "Employees kind of trust" when {

          "role in company not previously answered" in {
            val settlorsAnswers: ReadOnlyUserAnswers = ReadOnlyUserAnswers(
              emptyUserAnswers.set(KindOfTrustPage, Employees).value.data
            )

            mockRegistrationsRepositoryBuilder(getSettlorsAnswersResult = Right(Some(settlorsAnswers)))

            val userAnswers = emptyUserAnswers
              .set(individualPages.NamePage(0), FullName("Joe", None, "Bloggs")).value
              .set(individualPages.DateOfBirthYesNoPage(0), false).value
              .set(individualPages.IncomeYesNoPage(0), true).value
              .set(individualPages.NationalInsuranceYesNoPage(0), false).value
              .set(individualPages.AddressYesNoPage(0), false).value
              .set(individualPages.VulnerableYesNoPage(0), false).value
              .set(IndividualBeneficiaryStatus(0), Completed).value

            val application =
              applicationBuilder(userAnswers = Some(userAnswers), mockGetSettlorsAnswersResult = Right(Some(settlorsAnswers)))
                .build()

            when(mockTrustsStoreService.updateTaskStatus(any(), mEq(TaskStatus.InProgress))(any(), any()))
              .thenReturn(EitherT[Future, TrustErrors, Boolean](Future.successful(Right(true))))

            val request = FakeRequest(GET, addABeneficiaryRoute)

            val result = route(application, request).value

            val view = application.injector.instanceOf[AddABeneficiaryView]

            status(result) mustEqual OK

            lazy val inProgressRows = List(
              AddRow(
                name = "Joe Bloggs",
                typeLabel = "Named individual",
                changeUrl = individualRoutes.NameController.onPageLoad(0, fakeDraftId).url,
                removeUrl = individualRoutes.RemoveIndividualBeneficiaryController.onPageLoad(0, fakeDraftId).url
              )
            )

            contentAsString(result) mustEqual
              view(form, fakeDraftId, inProgressRows, Nil, "Add a beneficiary", Nil)(request, messages).toString

            application.stop()
          }

          "role in company previously answered for some but not others" in {
            val settlorsAnswers: ReadOnlyUserAnswers = ReadOnlyUserAnswers(
              emptyUserAnswers.set(KindOfTrustPage, Employees).value.data
            )

            mockRegistrationsRepositoryBuilder(getSettlorsAnswersResult = Right(Some(settlorsAnswers)))

            val userAnswers = emptyUserAnswers
              .set(individualPages.NamePage(0), FullName("Joe", None, "Bloggs")).value
              .set(individualPages.DateOfBirthYesNoPage(0), false).value
              .set(individualPages.IncomeYesNoPage(0), true).value
              .set(individualPages.NationalInsuranceYesNoPage(0), false).value
              .set(individualPages.AddressYesNoPage(0), false).value
              .set(individualPages.VulnerableYesNoPage(0), false).value
              .set(IndividualBeneficiaryStatus(0), Completed).value

              .set(individualPages.NamePage(1), FullName("John", None, "Doe")).value
              .set(individualPages.RoleInCompanyPage(1), Employee).value
              .set(individualPages.DateOfBirthYesNoPage(1), false).value
              .set(individualPages.IncomeYesNoPage(1), true).value
              .set(individualPages.NationalInsuranceYesNoPage(1), false).value
              .set(individualPages.AddressYesNoPage(1), false).value
              .set(individualPages.VulnerableYesNoPage(1), false).value
              .set(IndividualBeneficiaryStatus(1), Completed).value

            val application =
              applicationBuilder(userAnswers = Some(userAnswers), mockGetSettlorsAnswersResult = Right(Some(settlorsAnswers)))
                .build()

            when(mockTrustsStoreService.updateTaskStatus(any(), mEq(TaskStatus.InProgress))(any(), any()))
              .thenReturn(EitherT[Future, TrustErrors, Boolean](Future.successful(Right(true))))

            val request = FakeRequest(GET, addABeneficiaryRoute)

            val result = route(application, request).value

            val view = application.injector.instanceOf[AddABeneficiaryView]

            status(result) mustEqual OK

            lazy val inProgressRows = List(
              AddRow(
                name = "Joe Bloggs",
                typeLabel = "Named individual",
                changeUrl = individualRoutes.NameController.onPageLoad(0, fakeDraftId).url,
                removeUrl = individualRoutes.RemoveIndividualBeneficiaryController.onPageLoad(0, fakeDraftId).url
              )
            )

            lazy val completeRows = List(
              AddRow(
                name = "John Doe",
                typeLabel = "Named individual",
                changeUrl = individualRoutes.AnswersController.onPageLoad(1, fakeDraftId).url,
                removeUrl = individualRoutes.RemoveIndividualBeneficiaryController.onPageLoad(1, fakeDraftId).url
              )
            )

            contentAsString(result) mustEqual
              view(form, fakeDraftId, inProgressRows, completeRows, "You have added 2 beneficiaries", Nil)(request, messages).toString

            application.stop()
          }
        }
      }

      "YesNow selected" must {

        "redirect to the next page when valid data is submitted" in {

          val application =
            applicationBuilder(userAnswers = Some(userAnswersWithBeneficiariesComplete))
              .overrides(
                bind[TrustsStoreService].to(mockTrustsStoreService),
                bind[RegistrationProgress].to(mockRegistrationProgress)
              )
              .build()

          when(mockRegistrationProgress.beneficiariesStatus(any())).thenReturn(Some(InProgress))

          val request =
            FakeRequest(POST, addAnotherPostRoute)
              .withFormUrlEncodedBody(("value", AddABeneficiary.YesNow.toString))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

          verify(mockTrustsStoreService).updateTaskStatus(mEq(draftId), mEq(TaskStatus.InProgress))(any(), any())

          application.stop()
        }
      }

      "YesLater selected" must {

        "redirect to the next page when valid data is submitted" in {

          val application =
            applicationBuilder(userAnswers = Some(userAnswersWithBeneficiariesComplete))
              .overrides(
                bind[TrustsStoreService].to(mockTrustsStoreService),
                bind[RegistrationProgress].to(mockRegistrationProgress)
              )
              .build()

          when(mockRegistrationProgress.beneficiariesStatus(any())).thenReturn(Some(InProgress))

          val request =
            FakeRequest(POST, addAnotherPostRoute)
              .withFormUrlEncodedBody(("value", AddABeneficiary.YesLater.toString))

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

          verify(mockTrustsStoreService).updateTaskStatus(mEq(draftId), mEq(TaskStatus.InProgress))(any(), any())

          application.stop()
        }
      }

      "NoComplete selected" when {

        "registration is not complete" must {

          "redirect to the next page when valid data is submitted" in {

            val answersWithInProgress = userAnswersWithBeneficiariesComplete

            when(mockRegistrationProgress.beneficiariesStatus(any())).thenReturn(Some(InProgress))

            val application =
              applicationBuilder(userAnswers = Some(answersWithInProgress))
                .overrides(
                  bind[TrustsStoreService].to(mockTrustsStoreService),
                  bind[RegistrationProgress].to(mockRegistrationProgress)
                ).build()

            val request =
              FakeRequest(POST, addAnotherPostRoute)
                .withFormUrlEncodedBody(("value", AddABeneficiary.NoComplete.toString))

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

            verify(mockTrustsStoreService).updateTaskStatus(mEq(draftId), mEq(TaskStatus.InProgress))(any(), any())

            application.stop()
          }

        }

        "registration is complete" must {

          "redirect to the next page when valid data is submitted" in {

            val answersWithInProgress = userAnswersWithBeneficiariesComplete

            when(mockRegistrationProgress.beneficiariesStatus(any())).thenReturn(Some(Completed))

            val application =
              applicationBuilder(userAnswers = Some(answersWithInProgress))
                .overrides(
                  bind[TrustsStoreService].to(mockTrustsStoreService),
                  bind[RegistrationProgress].to(mockRegistrationProgress)
                ).build()

            val request =
              FakeRequest(POST, addAnotherPostRoute)
                .withFormUrlEncodedBody(("value", AddABeneficiary.NoComplete.toString))

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

            verify(mockTrustsStoreService).updateTaskStatus(mEq(draftId), mEq(TaskStatus.Completed))(any(), any())

            application.stop()
          }
        }
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
          view(boundForm, fakeDraftId, Nil, Nil, "Add a beneficiary", Nil)(request, messages).toString

        application.stop()
      }

    }

    "maxed out beneficiaries" must {

      "return correct view when all are maxed out" in {

        val beneficiaries = List(
          genTrustBeneficiaries(max),
          genIndividualBeneficiaries(max),
          genUnidentifiedBeneficiaries(max),
          genCompanyBeneficiaries(max),
          genCharityBeneficiaries(max),
          genLargeBeneficiaries(max),
          genOtherBeneficiaries(max)
        )

        val userAnswers = beneficiaries.foldLeft(emptyUserAnswers)((x, acc) => acc.copy(data = x.data.deepMerge(acc.data)))

        val beneficiaryRows = new AddABeneficiaryViewHelper(userAnswers, fakeDraftId).rows

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[MaxedOutBeneficiariesView]

        status(result) mustEqual OK

        val content = contentAsString(result)

        content mustEqual view(
          fakeDraftId,
          beneficiaryRows.inProgress,
          beneficiaryRows.complete,
          "You have added 175 beneficiaries"
        )(request, messages).toString

        content must include("You cannot enter another beneficiary as you have entered a maximum of 175.")
        content must include("If you have further beneficiaries to add, write to HMRC with their details.")

        application.stop()

      }

      "return correct view when one type of beneficiary is maxed out" in {

        val beneficiaries = List(
          genTrustBeneficiaries(0),
          genIndividualBeneficiaries(0),
          genUnidentifiedBeneficiaries(0),
          genCompanyBeneficiaries(0),
          genCharityBeneficiaries(max),
          genLargeBeneficiaries(0),
          genOtherBeneficiaries(0)
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
          genTrustBeneficiaries(0),
          genIndividualBeneficiaries(max),
          genUnidentifiedBeneficiaries(0),
          genCompanyBeneficiaries(0),
          genCharityBeneficiaries(max),
          genLargeBeneficiaries(0),
          genOtherBeneficiaries(0)
        )

        val userAnswers = beneficiaries.foldLeft(emptyUserAnswers)((x, acc) => acc.copy(data = x.data.deepMerge(acc.data)))

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        contentAsString(result) must include("You have entered the maximum number of beneficiaries for:")
        contentAsString(result) must include("If you have further beneficiaries to add within these types, write to HMRC with their details.")

        application.stop()

      }

      "redirect to registration progress when user clicks continue" when {

        "registration progress is complete" in {

          val beneficiaries = List(
            genTrustBeneficiaries(max),
            genIndividualBeneficiaries(max),
            genUnidentifiedBeneficiaries(max),
            genCompanyBeneficiaries(max),
            genCharityBeneficiaries(max),
            genLargeBeneficiaries(max),
            genOtherBeneficiaries(max)
          )

          val userAnswers = beneficiaries.foldLeft(emptyUserAnswers)((x, acc) => acc.copy(data = x.data.deepMerge(acc.data)))

          val application =
            applicationBuilder(userAnswers = Some(userAnswers))
              .overrides(
                bind[TrustsStoreService].to(mockTrustsStoreService),
                bind[RegistrationProgress].to(mockRegistrationProgress)
              )
              .build()

          when(mockRegistrationProgress.beneficiariesStatus(any())).thenReturn(Some(Completed))

          val request = FakeRequest(POST, submitCompleteRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual "http://localhost:9781/trusts-registration/draftId/registration-progress"

          verify(mockTrustsStoreService).updateTaskStatus(mEq(draftId), mEq(TaskStatus.Completed))(any(), any())

          application.stop()
        }

        "registration progress is not complete" in {

          val beneficiaries = List(
            genTrustBeneficiaries(max),
            genIndividualBeneficiaries(max),
            genUnidentifiedBeneficiaries(max),
            genCompanyBeneficiaries(max),
            genCharityBeneficiaries(max),
            genLargeBeneficiaries(max),
            genOtherBeneficiaries(max)
          )

          val userAnswers = beneficiaries.foldLeft(emptyUserAnswers)((x, acc) => acc.copy(data = x.data.deepMerge(acc.data)))

          val application =
            applicationBuilder(userAnswers = Some(userAnswers))
              .overrides(
                bind[TrustsStoreService].to(mockTrustsStoreService),
                bind[RegistrationProgress].to(mockRegistrationProgress)
              )
              .build()

          when(mockRegistrationProgress.beneficiariesStatus(any())).thenReturn(Some(InProgress))

          val request = FakeRequest(POST, submitCompleteRoute)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).value mustEqual "http://localhost:9781/trusts-registration/draftId/registration-progress"

          verify(mockTrustsStoreService).updateTaskStatus(mEq(draftId), mEq(TaskStatus.InProgress))(any(), any())

          application.stop()
        }


      }

    }

  }
}
