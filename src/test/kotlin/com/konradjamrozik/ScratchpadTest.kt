// Author: Konrad Jamrozik, github.com/konrad-jamrozik

package com.konradjamrozik

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.StringWriter

@RunWith(JUnit4::class)
 class ScratchpadTest {

  @Test
  fun `test`() {
    val sw = StringWriter()
    sw.write("widgets ")
    println(sw.toString())
  }
}

