// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package com.azure.android.communication.ui.callingcompositedemoapp.util

import androidx.test.platform.app.InstrumentationRegistry

object TestFixture {
    val teamsUrl by lazy {
        InstrumentationRegistry.getArguments().getString("teamsUrl") ?:
        "https://teams.microsoft.com/l/meetup-join/19%3ameeting_OTgyYWRhZTgtNTA0MS00NjNlLTliMTQtNDJhN2I3YjVmZTM5%40thread.v2/0?context=%7b%22Tid%22%3a%2272f988bf-86f1-41af-91ab-2d7cd011db47%22%2c%22Oid%22%3a%22009cb10a-d33f-4e2f-85eb-249a30042a51%22%7d"
    }

    val groupId by lazy {
        InstrumentationRegistry.getArguments().getString("groupId") ?:
        "74fce2c0-520f-11ec-97de-71411a9a8e13"
    }

    val acsToken by lazy {
        InstrumentationRegistry.getArguments().getString("acsToken") ?:
        "eyJhbGciOiJSUzI1NiIsImtpZCI6IjEwNCIsIng1dCI6IlJDM0NPdTV6UENIWlVKaVBlclM0SUl4Szh3ZyIsInR5cCI6IkpXVCJ9.eyJza3lwZWlkIjoiYWNzOmU1Y2M1ZGMwLTkwODMtNGFmZC1iYmMwLThhZGQ0MWVmODcwOF8wMDAwMDAwZi02NzA3LTVmODMtNjU1ZC01NzNhMGQwMDc5MzciLCJzY3AiOjE3OTIsImNzaSI6IjE2NDQwMTg2NDYiLCJleHAiOjE2NDQxMDUwNDYsImFjc1Njb3BlIjoidm9pcCIsInJlc291cmNlSWQiOiJlNWNjNWRjMC05MDgzLTRhZmQtYmJjMC04YWRkNDFlZjg3MDgiLCJpYXQiOjE2NDQwMTg2NDZ9.rnNk5STPXvtTRlLADoJ0e2ZD0IysNchqeJERyn-TZQrDAc9ka1vOzPr_jliQ4sdJDvMrCxKg9bTy6dCvwiMWp2yU0ZdxX6Jojr5uxLSQRaz3NTPA-Pd4HHKSGIjiVtq9XMv96yAMy1CSr3JjvAVRsuoDvnVsxxWyFFR4Ui3O0X8bJPlEbj7Kmb-Mn6SHivNIQyTGYjX99oKO3QY3dlqSrg9fLRb7C7KX0Wdd07pKMiFNFkkb47DDeDsdwTlD25z0u3x572RZGddlzooNwUrK6rMX4YDXT1U62p9F6r15GRGAqwjDnOTlhQnYS-S8oJ2qAiQSiG5-anNYBBKwF4PB6Q"
    }
}
