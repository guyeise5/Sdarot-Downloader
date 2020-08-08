from selenium import webdriver
from selenium.webdriver.common.keys import Keys
driver = webdriver.Firefox()
driver.get("https://www.sdarot.work/watch/4961-אדמה-מרה-bir-zamanlar-çukurova")
eps = driver.find_element_by_xpath("/html/body/div[2]/section[1]/div[2]/div/div[2]/ul[2]/li[1]")
eps.click()
sleep(30)
b_start = driver.find_element_by_xpath("/html/body/div[2]/section[4]/div/div[2]/button/span")
b_start.click()

vid = driver.find_element_by_xpath("/html/body/div[2]/section[2]/div[2]/div[2]/div/div[2]")
